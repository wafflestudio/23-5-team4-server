package com.wafflestudio.spring2025.domain.registration.dto.request

import com.wafflestudio.spring2025.domain.registration.model.RegistrationStatus

data class UpdateRegistrationStatusRequest(
    val status: RegistrationStatus? = null,
)
