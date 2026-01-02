package com.wafflestudio.spring2025.registration.dto.core

import com.wafflestudio.spring2025.registration.model.Registration
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "동아리 가입 신청 정보")
data class RegistrationDto(
    @Schema(description = "신청 ID")
    val id: Long,

    @Schema(description = "사용자 ID")
    val userId: Long,

    @Schema(description = "동아리 ID")
    val groupId: Long,

    @Schema(description = "신청 일시 (epoch milliseconds)")
    val createdAt: Long,
) {
    constructor(registration: Registration) : this(
        id = registration.id!!,
        userId = registration.userId,
        groupId = registration.groupId,
        createdAt = registration.createdAt!!.toEpochMilli(),
    )
}
