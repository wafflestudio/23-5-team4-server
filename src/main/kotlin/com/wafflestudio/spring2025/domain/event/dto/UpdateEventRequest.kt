package com.wafflestudio.spring2025.domain.event.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "일정 수정 요청")
data class UpdateEventRequest(
    @Schema(description = "일정 시작 시간 (epoch milliseconds)")
    val startTime: Long?,

    @Schema(description = "일정 종료 시간 (epoch milliseconds)")
    val endTime: Long?,

    @Schema(description = "신청 마감 시간 (epoch milliseconds)")
    val registerDeadline: Long?,

    @Schema(description = "일정 설명")
    val detail: String?,

    @Schema(description = "정원")
    val capacity: Int?,
)