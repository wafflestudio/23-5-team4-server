package com.wafflestudio.spring2025.domain.event.dto.core

import com.wafflestudio.spring2025.domain.event.model.Event
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "일정 정보")
data class EventDto(
    @Schema(description = "일정 ID")
    val id: Long,

    @Schema(description = "동아리 ID")
    val groupId: Long,

    @Schema(description = "일정 시작 시간 (epoch milliseconds)")
    val startTime: Long,

    @Schema(description = "일정 종료 시간 (epoch milliseconds)")
    val endTime: Long,

    @Schema(description = "신청 마감 시간 (epoch milliseconds)")
    val registerDeadline: Long,

    @Schema(description = "일정 설명")
    val detail: String,

    @Schema(description = "정원")
    val capacity: Int,
) {
    constructor(event: Event) : this(
        id = event.id!!,
        groupId = event.groupId,
        startTime = event.startTime.toEpochMilli(),
        endTime = event.endTime.toEpochMilli(),
        registerDeadline = event.registerDeadline.toEpochMilli(),
        detail = event.detail,
        capacity = event.capacity,
    )
}