package com.wafflestudio.spring2025.domain.registration.dto.core

import com.wafflestudio.spring2025.domain.registration.model.Registration
import com.wafflestudio.spring2025.domain.registration.model.RegistrationStatus

data class RegistrationDto(
    val registrationPublicId: String,
    val userId: Long?,
    val eventId: Long,
    val guestName: String?,
    val guestEmail: String?,
    val status: RegistrationStatus,
    val createdAt: Long,
) {
    constructor(registration: Registration) : this(
        registrationPublicId = registration.registrationPublicId,
        userId = registration.userId,
        eventId = registration.eventId,
        guestName = registration.guestName,
        guestEmail = registration.guestEmail,
        status = registration.status,
        createdAt = registration.createdAt!!.toEpochMilli(),
    )
}
