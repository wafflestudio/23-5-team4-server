package com.wafflestudio.spring2025.domain.event.controller

import com.wafflestudio.spring2025.domain.event.dto.CreateEventRequest
import com.wafflestudio.spring2025.domain.event.dto.UpdateEventRequest
import com.wafflestudio.spring2025.domain.event.dto.core.EventDto
import com.wafflestudio.spring2025.domain.event.service.EventService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/groups/{groupId}/events")
@Tag(name = "Event", description = "일정 관리 API")
class EventController(
    private val eventService: EventService,
) {
    @Operation(summary = "일정 생성", description = "새로운 일정을 생성합니다")
    @PostMapping
    fun create(
        @PathVariable groupId: Long,
        @RequestBody request: CreateEventRequest,
    ): ResponseEntity<EventDto> {
        TODO("일정 생성 API 구현")
    }

    @Operation(summary = "일정 목록 조회", description = "동아리의 모든 일정을 조회합니다")
    @GetMapping
    fun list(@PathVariable groupId: Long): ResponseEntity<List<EventDto>> {
        TODO("일정 목록 조회 API 구현")
    }

    @Operation(summary = "일정 상세 조회", description = "일정 상세 정보를 조회합니다")
    @GetMapping("/{eventId}")
    fun getById(
        @PathVariable groupId: Long,
        @PathVariable eventId: Long,
    ): ResponseEntity<EventDto> {
        TODO("일정 상세 조회 API 구현")
    }

    @Operation(summary = "일정 수정", description = "일정을 수정합니다")
    @PutMapping("/{eventId}")
    fun update(
        @PathVariable groupId: Long,
        @PathVariable eventId: Long,
        @RequestBody request: UpdateEventRequest,
    ): ResponseEntity<EventDto> {
        TODO("일정 수정 API 구현")
    }

    @Operation(summary = "일정 삭제", description = "일정을 삭제합니다")
    @DeleteMapping("/{eventId}")
    fun delete(
        @PathVariable groupId: Long,
        @PathVariable eventId: Long,
    ): ResponseEntity<Unit> {
        TODO("일정 삭제 API 구현")
    }

    @Operation(summary = "일정 참가 신청", description = "일정에 참가 신청을 합니다")
    @PostMapping("/{eventId}/register")
    fun register(
        @PathVariable groupId: Long,
        @PathVariable eventId: Long,
    ): ResponseEntity<Unit> {
        TODO("일정 참가 신청 API 구현")
    }

    @Operation(summary = "일정 참가 취소", description = "일정 참가를 취소합니다")
    @DeleteMapping("/{eventId}/register")
    fun unregister(
        @PathVariable groupId: Long,
        @PathVariable eventId: Long,
    ): ResponseEntity<Unit> {
        TODO("일정 참가 취소 API 구현")
    }
}