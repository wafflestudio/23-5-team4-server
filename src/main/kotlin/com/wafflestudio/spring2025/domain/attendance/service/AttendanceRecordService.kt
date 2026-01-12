package com.wafflestudio.spring2025.domain.attendance.service

import com.wafflestudio.spring2025.domain.attendance.dto.core.AttendanceRecordDto
import com.wafflestudio.spring2025.domain.attendance.model.AttendanceStatus
import com.wafflestudio.spring2025.domain.attendance.repository.AttendanceRecordRepository
import org.springframework.stereotype.Service

@Service
class AttendanceRecordService(
    private val attendanceRecordRepository: AttendanceRecordRepository,
) {
    fun record(userId: Long, eventId: Long, status: AttendanceStatus): AttendanceRecordDto {
        TODO("출석 기록 생성 구현")
    }

    fun getByEventId(eventId: Long): List<AttendanceRecordDto> {
        TODO("일정별 출석 기록 조회 구현")
    }

    fun getUserAttendanceScore(userId: Long): Int {
        TODO("사용자 출석 점수 계산 구현")
    }
}