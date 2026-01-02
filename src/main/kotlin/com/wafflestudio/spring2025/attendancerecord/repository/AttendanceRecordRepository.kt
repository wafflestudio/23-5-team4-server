package com.wafflestudio.spring2025.attendancerecord.repository

import com.wafflestudio.spring2025.attendancerecord.model.AttendanceRecord
import com.wafflestudio.spring2025.attendancerecord.model.AttendanceStatus
import org.springframework.data.repository.ListCrudRepository

interface AttendanceRecordRepository : ListCrudRepository<AttendanceRecord, Long> {
    fun findByEventId(eventId: Long): List<AttendanceRecord>
    fun findByUserId(userId: Long): List<AttendanceRecord>
    fun findByEventIdAndUserId(eventId: Long, userId: Long): AttendanceRecord?
    fun countByUserIdAndStatus(userId: Long, status: AttendanceStatus): Long
}
