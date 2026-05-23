package com.wafflestudio.spring2025.domain.registration.dto.response

import com.wafflestudio.spring2025.domain.registration.model.RegistrationStatus

data class GetRegistrationResponse(
    val status: RegistrationStatus,
    val guestName: String,
    val waitlistPosition: Int,
    val registrationPublicId: String,
    val reservationEmail: String,
)
