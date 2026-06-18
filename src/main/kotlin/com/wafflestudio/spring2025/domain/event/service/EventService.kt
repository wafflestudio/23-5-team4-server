package com.wafflestudio.spring2025.domain.event.service

import com.wafflestudio.spring2025.common.email.service.EmailService
import com.wafflestudio.spring2025.common.image.service.ImageService
import com.wafflestudio.spring2025.domain.event.dto.response.CapabilitiesInfo
import com.wafflestudio.spring2025.domain.event.dto.response.CreatorInfo
import com.wafflestudio.spring2025.domain.event.dto.response.EventDetailResponse
import com.wafflestudio.spring2025.domain.event.dto.response.EventInfo
import com.wafflestudio.spring2025.domain.event.dto.response.GuestPreview
import com.wafflestudio.spring2025.domain.event.dto.response.MyEventResponse
import com.wafflestudio.spring2025.domain.event.dto.response.MyEventsInfiniteResponse
import com.wafflestudio.spring2025.domain.event.dto.response.ViewerInfo
import com.wafflestudio.spring2025.domain.event.dto.response.ViewerStatus
import com.wafflestudio.spring2025.domain.event.exception.EventErrorCode
import com.wafflestudio.spring2025.domain.event.exception.EventForbiddenException
import com.wafflestudio.spring2025.domain.event.exception.EventNotFoundException
import com.wafflestudio.spring2025.domain.event.exception.EventValidationException
import com.wafflestudio.spring2025.domain.event.model.Event
import com.wafflestudio.spring2025.domain.event.repository.EventLockRepository
import com.wafflestudio.spring2025.domain.event.repository.EventRepository
import com.wafflestudio.spring2025.domain.registration.model.RegistrationStatus
import com.wafflestudio.spring2025.domain.registration.repository.RegistrationRepository
import com.wafflestudio.spring2025.domain.registration.service.WaitlistReconciliationService
import com.wafflestudio.spring2025.domain.user.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.Instant
import java.util.UUID

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val eventLockRepository: EventLockRepository,
    private val registrationRepository: RegistrationRepository,
    private val waitlistReconciliationService: WaitlistReconciliationService,
    private val userRepository: UserRepository,
    private val imageService: ImageService,
    private val emailService: EmailService,
) {
    /**
     * 일정 생성
     */
    fun create(
        title: String,
        description: String?,
        location: String?,
        startsAt: Instant?,
        endsAt: Instant?,
        capacity: Int?,
        waitlistEnabled: Boolean,
        registrationStartsAt: Instant?,
        registrationEndsAt: Instant,
        createdBy: Long,
    ): String {
        val actualRegistrationStartsAt = registrationStartsAt ?: Instant.now()

        validateCreateConstraints(registrationEndsAt)
        validateInvariantConstraints(
            title = title,
            capacity = capacity,
            startsAt = startsAt,
            endsAt = endsAt,
            registrationStartsAt = actualRegistrationStartsAt,
            registrationEndsAt = registrationEndsAt,
        )

        val event =
            Event(
                publicId = UUID.randomUUID().toString(),
                title = title.trim(),
                description = description,
                location = location,
                startsAt = startsAt,
                endsAt = endsAt,
                capacity = capacity,
                waitlistEnabled = waitlistEnabled,
                registrationStartsAt = actualRegistrationStartsAt,
                registrationEndsAt = registrationEndsAt,
                createdBy = createdBy,
            )

        return eventRepository.save(event).publicId
    }

    /**
     * 일정 상세 조회
     */
    fun getDetail(
        publicId: String,
        requesterId: Long?,
    ): EventDetailResponse {
        val event = getEventByPublicId(publicId)
        val eventId = requireNotNull(event.id) { "Event id is null: publicId=$publicId" }

        val userIdsToFetch = listOfNotNull(event.createdBy, requesterId).distinct()
        val usersById = userRepository.findAllById(userIdsToFetch).associateBy { it.id!! }
        val creatorUser = event.createdBy?.let { usersById[it] }

        val myReg =
            if (requesterId == null) {
                null
            } else {
                registrationRepository.findByUserIdAndEventId(
                    userId = requesterId,
                    eventId = eventId,
                )
            }

        val confirmedCount =
            registrationRepository
                .countByEventIdAndStatus(eventID = eventId, registrationStatus = RegistrationStatus.CONFIRMED)
                .toInt()

        val waitlistedCount =
            registrationRepository
                .countByEventIdAndStatus(eventID = eventId, registrationStatus = RegistrationStatus.WAITLISTED)
                .toInt()

        val waitlistPosition: Int? =
            if (myReg?.status == RegistrationStatus.WAITLISTED) {
                registrationRepository
                    .findWaitlistPositionsByRegistrationPublicIds(
                        eventId = eventId,
                        status = RegistrationStatus.WAITLISTED,
                        registrationPublicIds = listOf(myReg.registrationPublicId),
                    ).firstOrNull()
                    ?.waitlistNumber
                    ?.toInt()
            } else {
                null
            }

        val viewerStatus: ViewerStatus =
            when {
                requesterId == null -> ViewerStatus.NONE
                requesterId == event.createdBy -> ViewerStatus.HOST
                myReg == null -> ViewerStatus.NONE
                else ->
                    when (myReg.status) {
                        RegistrationStatus.HOST -> ViewerStatus.HOST
                        RegistrationStatus.CONFIRMED -> ViewerStatus.CONFIRMED
                        RegistrationStatus.WAITLISTED -> ViewerStatus.WAITLISTED
                        RegistrationStatus.BANNED -> ViewerStatus.BANNED
                    }
            }

        val viewerName: String? = requesterId?.let { usersById[it]?.name }

        val viewer =
            if (viewerStatus == ViewerStatus.NONE) {
                ViewerInfo(
                    status = ViewerStatus.NONE,
                    name = viewerName,
                    waitlistPosition = null,
                    registrationPublicId = null,
                    reservationEmail = null,
                )
            } else {
                ViewerInfo(
                    status = viewerStatus,
                    name = viewerName,
                    waitlistPosition = waitlistPosition,
                    registrationPublicId = myReg?.registrationPublicId,
                    reservationEmail = myReg?.guestEmail,
                )
            }

        val capabilities =
            buildCapabilities(
                viewerStatus = viewerStatus,
                capacity = event.capacity,
                confirmedCount = confirmedCount,
                waitlistEnabled = event.waitlistEnabled,
                registrationStartsAt = event.registrationStartsAt,
                registrationEndsAt = event.registrationEndsAt,
            )

        val previewRegs =
            registrationRepository.findByEventIdAndStatusOrderByCreatedAtAsc(
                eventID = eventId,
                registrationStatus = RegistrationStatus.CONFIRMED,
                pageable = Pageable.ofSize(5),
            )

        val previewUserIds =
            previewRegs.mapNotNull { it.userId }.distinct()

        val previewUsersById =
            userRepository.findAllById(previewUserIds).associateBy { it.id!! }

        val guestsPreview =
            previewRegs.mapNotNull { reg ->
                val uid = reg.userId
                if (uid == null) {
                    GuestPreview(
                        id = null,
                        name = reg.guestName ?: "참여자",
                        profileImage = null,
                    )
                } else {
                    previewUsersById[uid]?.let {
                        GuestPreview(
                            id = it.id!!,
                            name = it.name,
                            profileImage = it.profileImage?.let { key -> imageService.presignedGetUrl(key) },
                        )
                    }
                }
            }

        return EventDetailResponse(
            event =
                EventInfo(
                    publicId = event.publicId,
                    title = event.title,
                    description = event.description,
                    location = event.location,
                    startsAt = event.startsAt,
                    endsAt = event.endsAt,
                    capacity = event.capacity,
                    confirmedCount = confirmedCount,
                    waitlistCount = waitlistedCount,
                    registrationStartsAt = event.registrationStartsAt,
                    registrationEndsAt = event.registrationEndsAt,
                ),
            creator =
                CreatorInfo(
                    name = creatorUser?.name ?: "탈퇴유저",
                    email = creatorUser?.email,
                    profileImage = creatorUser?.profileImage?.let { imageService.presignedGetUrl(it) },
                ),
            viewer = viewer,
            capabilities = capabilities,
            guestsPreview = guestsPreview,
        )
    }

    /**
     * 내가 만든 일정 조회 (무한 스크롤)
     */
    fun getMyEventsInfinite(
        createdBy: Long,
        cursor: Instant?,
        size: Int,
    ): MyEventsInfiniteResponse {
        val pageSize = size.coerceAtLeast(1)
        val pageable =
            PageRequest.of(
                0,
                pageSize + 1,
                Sort.by(Sort.Direction.DESC, "createdAt"),
            )

        val fetched =
            if (cursor == null) {
                eventRepository.findByCreatedByAndCreatedAtIsNotNullOrderByCreatedAtDesc(createdBy, pageable)
            } else {
                eventRepository.findByCreatedByAndCreatedAtIsNotNullAndCreatedAtLessThanOrderByCreatedAtDesc(
                    createdBy = createdBy,
                    cursor = cursor,
                    pageable = pageable,
                )
            }

        val hasNext = fetched.size > pageSize
        val sliced = fetched.take(pageSize)
        val nextCursor = sliced.lastOrNull()?.createdAt

        val eventIds = sliced.map { requireNotNull(it.id) }

        val confirmedCounts =
            if (eventIds.isEmpty()) {
                emptyMap()
            } else {
                registrationRepository
                    .countByEventIdsAndStatuses(eventIds = eventIds, listOf(RegistrationStatus.CONFIRMED))
                    .associate { it.eventId to it.totalCount.toInt() }
            }

        val waitlistedCounts =
            if (eventIds.isEmpty()) {
                emptyMap()
            } else {
                registrationRepository
                    .countByEventIdsAndStatuses(eventIds = eventIds, listOf(RegistrationStatus.WAITLISTED))
                    .associate { it.eventId to it.totalCount.toInt() }
            }

        val responses =
            sliced.map { event ->
                val eventId = requireNotNull(event.id)
                val confirmedCount = confirmedCounts[eventId] ?: 0
                val waitlistedCount = waitlistedCounts[eventId] ?: 0

                MyEventResponse(
                    publicId = event.publicId,
                    title = event.title,
                    startsAt = event.startsAt,
                    endsAt = event.endsAt,
                    registrationStartsAt = event.registrationStartsAt,
                    registrationEndsAt = event.registrationEndsAt,
                    capacity = event.capacity,
                    confirmedCount = confirmedCount,
                    waitlistCount = waitlistedCount,
                )
            }

        return MyEventsInfiniteResponse(
            events = responses,
            nextCursor = if (hasNext) nextCursor else null,
            hasNext = hasNext,
        )
    }

    @Transactional
    fun update(
        publicId: String,
        title: String?,
        description: String?,
        location: String?,
        startsAt: Instant?,
        endsAt: Instant?,
        capacity: Int?,
        waitlistEnabled: Boolean?,
        registrationStartsAt: Instant?,
        registrationEndsAt: Instant?,
        requesterId: Long,
    ): Event {
        val eventForAuth = getEventByPublicId(publicId)
        requireCreator(eventForAuth, requesterId)
        val eventId = requireNotNull(eventForAuth.id) { "Event id is null: publicId=$publicId" }
        if (!eventLockRepository.lockById(eventId)) throw EventNotFoundException()
        val event = eventRepository.findById(eventId).orElseThrow { EventNotFoundException() }
        val previousCapacity = event.capacity

        val mergedTitle = title?.trim() ?: event.title
        val mergedCapacity = capacity ?: event.capacity
        val mergedStartsAt = startsAt ?: event.startsAt
        val mergedEndsAt = endsAt ?: event.endsAt
        val mergedRegistrationStartsAt = registrationStartsAt ?: event.registrationStartsAt
        val mergedRegistrationEndsAt = registrationEndsAt ?: event.registrationEndsAt

        validateInvariantConstraints(
            title = mergedTitle,
            capacity = mergedCapacity,
            startsAt = mergedStartsAt,
            endsAt = mergedEndsAt,
            registrationStartsAt = mergedRegistrationStartsAt,
            registrationEndsAt = mergedRegistrationEndsAt,
        )

        event.title = mergedTitle
        description?.let { event.description = it }
        location?.let { event.location = it }
        event.startsAt = mergedStartsAt
        event.endsAt = mergedEndsAt
        event.capacity = mergedCapacity
        waitlistEnabled?.let { event.waitlistEnabled = it }
        event.registrationStartsAt = mergedRegistrationStartsAt
        event.registrationEndsAt = mergedRegistrationEndsAt

        val savedEvent = eventRepository.save(event)
        if (isCapacityIncreased(previousCapacity, savedEvent.capacity)) {
            waitlistReconciliationService.reconcileWaitlist(eventId)
        }
        if (isCapacityDecreased(previousCapacity, savedEvent.capacity)) {
            waitlistReconciliationService.demoteToWaitlist(eventId, savedEvent.capacity!!)
        }
        return savedEvent
    }

    @Transactional
    fun delete(
        publicId: String,
        requesterId: Long,
    ) {
        val eventId = eventLockRepository.lockIdByPublicId(publicId) ?: throw EventNotFoundException()
        val event = eventRepository.findById(eventId).orElseThrow { EventNotFoundException() }
        requireCreator(event, requesterId)

        // 알림 대상: CONFIRMED + WAITLISTED (BANNED 제외)
        val registrationsToNotify =
            registrationRepository.findByEventIdAndStatusIn(
                eventID = eventId,
                statuses = listOf(RegistrationStatus.CONFIRMED, RegistrationStatus.WAITLISTED),
            )

        // 이메일 데이터 구성 (삭제 전에 user 정보 조회)
        val hostUser = event.createdBy?.let { userRepository.findById(it).orElse(null) }
        val userIds = registrationsToNotify.mapNotNull { it.userId }.distinct()
        val usersById = userRepository.findAllById(userIds).associateBy { it.id!! }

        val emailDataList =
            registrationsToNotify.mapNotNull { reg ->
                val user = reg.userId?.let { usersById[it] }
                val toEmail = user?.email ?: reg.guestEmail
                if (toEmail.isNullOrBlank()) return@mapNotNull null
                EmailService.EventCancellationEmailData(
                    toEmail = toEmail,
                    name = user?.name ?: reg.guestName ?: "참여자",
                    eventTitle = event.title,
                    startsAt = event.startsAt,
                    endsAt = event.endsAt,
                    location = event.location,
                    description = event.description,
                    hostEmail = hostUser?.email,
                )
            }

        // FK 제약으로 인해 event 삭제 전 registrations 먼저 삭제
        registrationRepository.deleteByEventId(eventId)
        eventRepository.deleteById(eventId)

        afterCommit {
            emailDataList.forEach { data ->
                emailService.sendEventCancellationEmail(data)
            }
        }
    }

    /**
     * 유저 탈퇴 시 호출. 시작 전(now < startsAt) 이벤트들을 일괄 삭제하고
     * 참여자/대기자에게 익명화된 주최자 정보로 취소 메일을 발송한다.
     * 소유권 체크는 호출 측에서 보장.
     */
    @Transactional
    fun deleteEventsForWithdrawal(events: List<Event>) {
        if (events.isEmpty()) return
        val eventIds = events.map { it.id!! }.sorted()

        // 락 획득 (ID 오름차순으로 deadlock 방지)
        eventIds.forEach { eventLockRepository.lockById(it) }

        val eventsById = events.associateBy { it.id!! }
        val registrations =
            registrationRepository.findByEventIdInAndStatusIn(
                eventIds = eventIds,
                statuses = listOf(RegistrationStatus.CONFIRMED, RegistrationStatus.WAITLISTED),
            )
        val userIds = registrations.mapNotNull { it.userId }.distinct()
        val usersById = userRepository.findAllById(userIds).associateBy { it.id!! }

        val emailDataList =
            registrations.mapNotNull { reg ->
                val event = eventsById[reg.eventId] ?: return@mapNotNull null
                val user = reg.userId?.let { usersById[it] }
                val toEmail = user?.email ?: reg.guestEmail
                if (toEmail.isNullOrBlank()) return@mapNotNull null
                EmailService.EventCancellationEmailData(
                    toEmail = toEmail,
                    name = user?.name ?: reg.guestName ?: "참여자",
                    eventTitle = event.title,
                    startsAt = event.startsAt,
                    endsAt = event.endsAt,
                    location = event.location,
                    description = event.description,
                    hostEmail = null,
                )
            }

        registrationRepository.deleteByEventIdIn(eventIds)
        eventRepository.deleteAllById(eventIds)

        afterCommit {
            emailDataList.forEach { emailService.sendEventCancellationEmail(it) }
        }
    }

    private fun getEventByPublicId(publicId: String): Event =
        eventRepository.findByPublicId(publicId)
            ?: throw EventNotFoundException()

    private fun requireCreator(
        event: Event,
        requesterId: Long,
    ) {
        if (event.createdBy != requesterId) {
            throw EventForbiddenException()
        }
    }

    private fun validateCreateConstraints(
        registrationEndsAt: Instant,
        now: Instant = Instant.now(),
    ) {
        if (registrationEndsAt.isBefore(now)) {
            throw EventValidationException(EventErrorCode.REGISTRATION_ENDS_IN_PAST)
        }
    }

    private fun validateInvariantConstraints(
        title: String,
        capacity: Int?,
        startsAt: Instant?,
        endsAt: Instant?,
        registrationStartsAt: Instant?,
        registrationEndsAt: Instant,
    ) {
        // 제목 검증
        if (title.isBlank()) {
            throw EventValidationException(EventErrorCode.EVENT_TITLE_BLANK)
        }

        // 정원 검증
        if (capacity == null) {
            throw EventValidationException(EventErrorCode.EVENT_CAPACITY_REQUIRED)
        }
        if (capacity <= 0) {
            throw EventValidationException(EventErrorCode.EVENT_CAPACITY_INVALID)
        }

        // 모임 시간 검증: 일정 시작 ≤ 일정 끝
        if (startsAt != null && endsAt != null && startsAt.isAfter(endsAt)) {
            throw EventValidationException(EventErrorCode.EVENT_TIME_RANGE_INVALID)
        }

        // 신청 기간 검증: 모집 시작 < 모집 마감 (strict)
        if (registrationStartsAt != null && !registrationStartsAt.isBefore(registrationEndsAt)) {
            throw EventValidationException(EventErrorCode.REGISTRATION_TIME_RANGE_INVALID)
        }
        // 모집 시작 ≤ 일정 시작
        if (registrationStartsAt != null && startsAt != null && registrationStartsAt.isAfter(startsAt)) {
            throw EventValidationException(EventErrorCode.REGISTRATION_STARTS_AFTER_EVENT_START)
        }
        // 모집 마감 ≤ 일정 시작
        if (startsAt != null && registrationEndsAt.isAfter(startsAt)) {
            throw EventValidationException(EventErrorCode.REGISTRATION_ENDS_AFTER_EVENT_START)
        }
        // 모집 마감 ≤ 일정 끝 (행사시작 없을 때도 직접 체크)
        if (endsAt != null && registrationEndsAt.isAfter(endsAt)) {
            throw EventValidationException(EventErrorCode.REGISTRATION_ENDS_AFTER_EVENT_END)
        }
    }

    private fun isCapacityIncreased(
        previousCapacity: Int?,
        newCapacity: Int?,
    ): Boolean = previousCapacity != null && newCapacity != null && newCapacity > previousCapacity

    private fun isCapacityDecreased(
        previousCapacity: Int?,
        newCapacity: Int?,
    ): Boolean = previousCapacity != null && newCapacity != null && newCapacity < previousCapacity

    private fun afterCommit(action: () -> Unit) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            action()
            return
        }
        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCommit() {
                    action()
                }
            },
        )
    }

    private fun buildCapabilities(
        viewerStatus: ViewerStatus,
        capacity: Int?,
        confirmedCount: Int,
        waitlistEnabled: Boolean,
        registrationStartsAt: Instant?,
        registrationEndsAt: Instant,
        now: Instant = Instant.now(),
    ): CapabilitiesInfo {
        val withinWindow =
            (registrationStartsAt?.let { !now.isBefore(it) } ?: true) &&
                !now.isAfter(registrationEndsAt)

        val isFull =
            capacity != null && confirmedCount >= capacity

        // 확정 자리 신청 가능
        val canApply = withinWindow && !isFull

        // 대기 신청 가능: 정원 찼고 + 대기 허용
        val canWait = withinWindow && isFull && waitlistEnabled

        return when (viewerStatus) {
            ViewerStatus.HOST ->
                CapabilitiesInfo(
                    shareLink = true,
                    apply = false,
                    wait = false,
                    cancel = false,
                )

            ViewerStatus.CONFIRMED, ViewerStatus.WAITLISTED ->
                CapabilitiesInfo(
                    shareLink = false,
                    apply = false,
                    wait = false,
                    cancel = true,
                )

            ViewerStatus.NONE ->
                CapabilitiesInfo(
                    shareLink = false,
                    apply = canApply,
                    wait = canWait,
                    cancel = false,
                )

            ViewerStatus.BANNED ->
                CapabilitiesInfo(
                    shareLink = false,
                    apply = false,
                    wait = false,
                    cancel = false,
                )
        }
    }
}
