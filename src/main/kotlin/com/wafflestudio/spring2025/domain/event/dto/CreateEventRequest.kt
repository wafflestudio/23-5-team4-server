package com.wafflestudio.spring2025.domain.event.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "일정 생성 요청")
data class CreateEventRequest(
    @Schema(description = "일정 시작 시간 (epoch milliseconds)", example = "1700000000000")
    val startTime: Long,

    @Schema(description = "일정 종료 시간 (epoch milliseconds)", example = "1700003600000")
    val endTime: Long,

    @Schema(description = "신청 마감 시간 (epoch milliseconds)", example = "1699900000000")
    val registerDeadline: Long,

    @Schema(description = "일정 설명", example = "정기 모임")
    val detail: String,

    @Schema(description = "정원", example = "30")
    val capacity: Int,
)