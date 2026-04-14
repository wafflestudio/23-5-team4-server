package com.wafflestudio.spring2025.domain.registration.dto.request

import com.fasterxml.jackson.annotation.JsonAlias

data class CreateRegistrationRequest(
    @JsonAlias("guest_name")
    val guestName: String? = null,
    @JsonAlias("guest_email")
    val guestEmail: String? = null,
)
