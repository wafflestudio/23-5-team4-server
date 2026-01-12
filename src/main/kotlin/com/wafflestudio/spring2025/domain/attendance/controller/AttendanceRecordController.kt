package com.wafflestudio.spring2025.domain.attendance.controller

import com.wafflestudio.spring2025.domain.attendance.dto.core.AttendanceRecordDto
import com.wafflestudio.spring2025.domain.attendance.model.AttendanceStatus
import com.wafflestudio.spring2025.domain.attendance.service.AttendanceRecordService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/events/{eventId}/attendance")
@Tag(name = "AttendanceRecord", description = "출석 관리 API")
class AttendanceRecordController(
    private val attendanceRecordService: AttendanceRecordService,
) {
    @Operation(summary = "출석 기록", description = "일정에 대한 출석을 기록합니다")
    @PostMapping
    fun record(
        @PathVariable eventId: Long,
        @RequestParam status: AttendanceStatus,
    ): ResponseEntity<AttendanceRecordDto> {
        TODO("출석 기록 API 구현")
    }

    @Operation(summary = "출석 기록 조회", description = "일정의 출석 기록을 조회합니다")
    @GetMapping
    fun list(@PathVariable eventId: Long): ResponseEntity<List<AttendanceRecordDto>> {
        TODO("출석 기록 조회 API 구현")
    }
}