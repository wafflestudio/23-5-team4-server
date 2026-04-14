package com.wafflestudio.spring2025.domain.registration.dto.request

data class DeleteRegistrationRequest(
    val guestName: String? = null,
    val guestEmail: String? = null,
)
