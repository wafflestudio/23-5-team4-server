package com.wafflestudio.spring2025.domain.user.service

import com.wafflestudio.spring2025.common.image.service.ImageService
import com.wafflestudio.spring2025.config.AwsS3Properties
import com.wafflestudio.spring2025.domain.auth.exception.AuthErrorCode
import com.wafflestudio.spring2025.domain.auth.exception.AuthValidationException
import com.wafflestudio.spring2025.domain.auth.exception.AuthenticationRequiredException
import com.wafflestudio.spring2025.domain.event.repository.EventRepository
import com.wafflestudio.spring2025.domain.event.service.EventService
import com.wafflestudio.spring2025.domain.registration.model.RegistrationStatus
import com.wafflestudio.spring2025.domain.registration.repository.RegistrationRepository
import com.wafflestudio.spring2025.domain.registration.service.RegistrationService
import com.wafflestudio.spring2025.domain.user.dto.core.UserDto
import com.wafflestudio.spring2025.domain.user.exception.EmailChangeForbiddenException
import com.wafflestudio.spring2025.domain.user.exception.UserErrorCode
import com.wafflestudio.spring2025.domain.user.exception.UserException
import com.wafflestudio.spring2025.domain.user.exception.UserValidationException
import com.wafflestudio.spring2025.domain.user.identity.repository.UserIdentityRepository
import com.wafflestudio.spring2025.domain.user.model.User
import com.wafflestudio.spring2025.domain.user.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import java.time.Instant

@Service
class UserService(
    private val userRepository: UserRepository,
    private val s3Client: S3Client,
    private val s3Props: AwsS3Properties,
    private val imageService: ImageService,
    private val eventRepository: EventRepository,
    private val registrationRepository: RegistrationRepository,
    private val userIdentityRepository: UserIdentityRepository,
    private val eventService: EventService,
    private val registrationService: RegistrationService,
) {
    fun me(user: User?): UserDto {
        if (user == null) throw AuthenticationRequiredException()

        val presignedUrl = user.profileImage?.let { imageService.presignedGetUrl(it) }

        return UserDto(
            id = user.id!!,
            email = user.email,
            name = user.name,
            profileImage = presignedUrl,
        )
    }

    fun patchMe(
        user: User,
        name: String?,
        email: String?,
        profileImage: String?,
        password: String?,
    ) {
        if (email != null) {
            throw EmailChangeForbiddenException()
        }
        name?.let { validateName(it) }
        password?.let { validatePassword(it) }
        profileImage?.let { validateProfileImage(it) }

        name?.let { user.name = it }
        password?.let { user.passwordHash = BCrypt.hashpw(it, BCrypt.gensalt()) }
        profileImage?.let { user.profileImage = it }

        userRepository.save(user)
    }

    @Transactional
    fun deleteMe(user: User) {
        val now = Instant.now()
        val userId = user.id ?: throw UserException(UserErrorCode.NO_SUCH_USER)

        // 1. 주최 이벤트 처리
        val hostedEvents = eventRepository.findByCreatedBy(userId)
        val (startedEvents, notStartedEvents) =
            hostedEvents.partition { it.startsAt != null && !now.isBefore(it.startsAt) }

        // 시작된 이벤트: Event + HOST 등록 익명화
        if (startedEvents.isNotEmpty()) {
            val startedIds = startedEvents.map { it.id!! }
            startedEvents.forEach { it.createdBy = null }
            eventRepository.saveAll(startedEvents)

            val hostRegs =
                registrationRepository.findByEventIdInAndStatusIn(
                    eventIds = startedIds,
                    statuses = listOf(RegistrationStatus.HOST),
                )
            hostRegs.forEach {
                it.userId = null
                it.guestName = "탈퇴유저"
                it.guestEmail = null
            }
            registrationRepository.saveAll(hostRegs)
        }

        // 시작 전 이벤트: 일괄 삭제 + 취소 메일 (락은 EventService 내부에서 획득)
        eventService.deleteEventsForWithdrawal(notStartedEvents)

        // 2. 참여 등록 처리 (CONFIRMED + WAITLISTED)
        val regs =
            registrationRepository.findByUserIdAndStatusIn(
                userId = userId,
                statuses = listOf(RegistrationStatus.CONFIRMED, RegistrationStatus.WAITLISTED),
            )
        if (regs.isNotEmpty()) {
            val eventsById =
                eventRepository
                    .findAllById(regs.map { it.eventId }.distinct())
                    .associateBy { it.id!! }
            // 고아 등록(이벤트 없음)은 skip
            val valid = regs.filter { eventsById.containsKey(it.eventId) }
            val (toAnonymize, toCancel) =
                valid.partition { reg -> !now.isBefore(eventsById[reg.eventId]!!.registrationEndsAt) }

            // 신청기간 마감: 익명화
            toAnonymize.forEach {
                it.userId = null
                it.guestName = "탈퇴유저"
                it.guestEmail = null
            }
            registrationRepository.saveAll(toAnonymize)

            // 신청기간 내: 일괄 취소 + 대기자 승격
            registrationService.cancelRegistrationsForWithdrawal(toCancel)
        }

        // 2b. BANNED 등록 익명화 (FK 정리)
        val bannedRegs =
            registrationRepository.findByUserIdAndStatusIn(
                userId = userId,
                statuses = listOf(RegistrationStatus.BANNED),
            )
        bannedRegs.forEach {
            it.userId = null
            it.guestName = "탈퇴유저"
            it.guestEmail = null
        }
        registrationRepository.saveAll(bannedRegs)

        // 3. UserIdentity 삭제
        userIdentityRepository.deleteByUserId(userId)

        // 4. User 삭제
        userRepository.delete(user)
    }

    private fun validateName(name: String) {
        if (name.isBlank()) {
            throw AuthValidationException(AuthErrorCode.BAD_NAME)
        }
    }

    private fun validatePassword(password: String) {
        if (password.length < 8) {
            throw AuthValidationException(AuthErrorCode.BAD_PASSWORD)
        }
        if (!password.any { it.isLetter() }) {
            throw AuthValidationException(AuthErrorCode.BAD_PASSWORD)
        }
        if (!password.any { it.isDigit() }) {
            throw AuthValidationException(AuthErrorCode.BAD_PASSWORD)
        }
    }

    private fun validateProfileImage(profileImageKey: String) {
        try {
            s3Client.headObject(
                HeadObjectRequest
                    .builder()
                    .bucket(s3Props.bucket)
                    .key(profileImageKey)
                    .build(),
            )
        } catch (_: NoSuchKeyException) {
            throw UserValidationException(UserErrorCode.PROFILE_IMAGE_NOT_FOUND)
        }
    }
}
