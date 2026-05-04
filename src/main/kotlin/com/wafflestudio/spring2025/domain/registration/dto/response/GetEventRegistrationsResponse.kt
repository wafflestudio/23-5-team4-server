package com.wafflestudio.spring2025.domain.registration.dto.response

import com.wafflestudio.spring2025.domain.registration.model.Registration
import com.wafflestudio.spring2025.domain.registration.model.RegistrationStatus
import com.wafflestudio.spring2025.domain.user.model.User
import java.time.Instant

data class GetEventRegistrationsResponse(
    val participants: List<EventRegistrationItem>,
    val totalCount: Int?,
    val nextCursor: Int?,
    val hasNext: Boolean,
)

data class EventRegistrationItem(
    val registrationId: String,
    val name: String,
    val email: String?,
    val profileImage: String?,
    val createdAt: Instant,
    val status: RegistrationStatus,
    val waitingNum: Int?,
) {
    constructor(
        registration: Registration,
        profileImage: String?,
        user: User?,
        waitingNum: Int?,
    ) : this(
        registrationId = registration.registrationPublicId,
        name = registration.guestName ?: user?.name.orEmpty(),
        email = registration.guestEmail ?: user?.email,
        profileImage = profileImage,
        createdAt = registration.createdAt!!,
        status = registration.status,
        waitingNum = waitingNum,
    )
}
