package com.wafflestudio.spring2025.attendancerecord.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("attendance_records")
class AttendanceRecord(
    @Id var id: Long? = null,
    var userId: Long,
    var eventId: Long,
    var status: AttendanceStatus,
    @CreatedDate
    var createdAt: Instant? = null,
)
