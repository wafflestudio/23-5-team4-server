package com.wafflestudio.spring2025

import com.wafflestudio.spring2025.domain.auth.JwtTokenProvider
import com.wafflestudio.spring2025.domain.event.model.Event
import com.wafflestudio.spring2025.domain.event.repository.EventRepository
import com.wafflestudio.spring2025.domain.registration.model.Registration
import com.wafflestudio.spring2025.domain.registration.model.RegistrationStatus
import com.wafflestudio.spring2025.domain.registration.repository.RegistrationRepository
import com.wafflestudio.spring2025.domain.user.model.User
import com.wafflestudio.spring2025.domain.user.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class DataGenerator(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val eventRepository: EventRepository,
    private val registrationRepository: RegistrationRepository,
) {
    fun generateUser(): Pair<User, String> {
        val email = "user-${UUID.randomUUID()}@example.com"
        val user =
            userRepository.save(
                User(
                    email = email,
                    name = "user",
                    profileImage = null,
                ),
            )
        val token = jwtTokenProvider.createToken(user.id!!)
        return user to token
    }

    fun generateUserWithPassword(password: String = "testPassword123"): Pair<User, String> {
        val email = "user-${UUID.randomUUID()}@example.com"
        val user =
            userRepository.save(
                User(
                    email = email,
                    name = "user",
                    passwordHash = BCrypt.hashpw(password, BCrypt.gensalt()),
                    profileImage = null,
                ),
            )
        val token = jwtTokenProvider.createToken(user.id!!)
        return user to token
    }

    fun generateUserWithProfileImage(profileImageKey: String): Pair<User, String> {
        val email = "user-${UUID.randomUUID()}@example.com"
        val user =
            userRepository.save(
                User(
                    email = email,
                    name = "user",
                    profileImage = profileImageKey,
                ),
            )
        val token = jwtTokenProvider.createToken(user.id!!)
        return user to token
    }

    fun createEvent(
        createdBy: Long,
        title: String = "테스트 이벤트",
        capacity: Int = 10,
        waitlistEnabled: Boolean = true,
        startsAt: Instant = Instant.now().plusSeconds(7200),
        endsAt: Instant = Instant.now().plusSeconds(10800),
        registrationStartsAt: Instant? = null,
        registrationEndsAt: Instant = Instant.now().plusSeconds(5400),
    ): Event =
        eventRepository.save(
            Event(
                publicId = UUID.randomUUID().toString(),
                title = title,
                capacity = capacity,
                waitlistEnabled = waitlistEnabled,
                startsAt = startsAt,
                endsAt = endsAt,
                registrationStartsAt = registrationStartsAt,
                registrationEndsAt = registrationEndsAt,
                createdBy = createdBy,
            ),
        )

    fun createRegistration(
        eventId: Long,
        userId: Long? = null,
        guestName: String? = null,
        guestEmail: String? = null,
        status: RegistrationStatus = RegistrationStatus.CONFIRMED,
    ): Registration =
        registrationRepository.save(
            Registration(
                userId = userId,
                eventId = eventId,
                guestName = guestName,
                guestEmail = guestEmail,
                status = status,
            ),
        )
}
