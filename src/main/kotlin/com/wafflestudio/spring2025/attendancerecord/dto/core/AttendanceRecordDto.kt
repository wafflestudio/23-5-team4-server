package com.wafflestudio.spring2025.attendancerecord.dto.core

import com.wafflestudio.spring2025.attendancerecord.model.AttendanceRecord
import com.wafflestudio.spring2025.attendancerecord.model.AttendanceStatus
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "출석 기록 정보")
data class AttendanceRecordDto(
    @Schema(description = "출석 기록 ID")
    val id: Long,

    @Schema(description = "사용자 ID")
    val userId: Long,

    @Schema(description = "일정 ID")
    val eventId: Long,

    @Schema(description = "출석 상태", example = "PRESENT")
    val status: AttendanceStatus,

    @Schema(description = "생성 일시 (epoch milliseconds)")
    val createdAt: Long,
) {
    constructor(record: AttendanceRecord) : this(
        id = record.id!!,
        userId = record.userId,
        eventId = record.eventId,
        status = record.status,
        createdAt = record.createdAt!!.toEpochMilli(),
    )
}
