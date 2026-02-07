package com.wafflestudio.spring2025.domain.registration.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "이벤트 신청 응답")
data class CreateRegistrationResponse(
    @Schema(description = "신청 id")
    val registrationPublicId: String,
)
