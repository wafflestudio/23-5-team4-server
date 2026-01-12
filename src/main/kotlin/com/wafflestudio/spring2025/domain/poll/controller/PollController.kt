package com.wafflestudio.spring2025.domain.poll.controller

import com.wafflestudio.spring2025.domain.poll.dto.CreatePollRequest
import com.wafflestudio.spring2025.domain.poll.dto.core.PollDto
import com.wafflestudio.spring2025.domain.poll.service.PollService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/groups/{groupId}/polls")
@Tag(name = "Poll", description = "투표 관리 API")
class PollController(
    private val pollService: PollService,
) {
    @Operation(summary = "투표 생성", description = "새로운 투표를 생성합니다")
    @PostMapping
    fun create(
        @PathVariable groupId: Long,
        @RequestBody request: CreatePollRequest,
    ): ResponseEntity<PollDto> {
        TODO("투표 생성 API 구현")
    }

    @Operation(summary = "투표 목록 조회", description = "동아리의 투표 목록을 조회합니다")
    @GetMapping
    fun list(
        @PathVariable groupId: Long,
        @RequestParam(required = false) onlyActive: Boolean = false,
    ): ResponseEntity<List<PollDto>> {
        TODO("투표 목록 조회 API 구현")
    }

    @Operation(summary = "투표 상세 조회", description = "투표 상세 정보를 조회합니다")
    @GetMapping("/{pollId}")
    fun getById(
        @PathVariable groupId: Long,
        @PathVariable pollId: Long,
    ): ResponseEntity<PollDto> {
        TODO("투표 상세 조회 API 구현")
    }

    @Operation(summary = "투표하기", description = "투표 선택지를 선택합니다")
    @PostMapping("/{pollId}/vote")
    fun vote(
        @PathVariable groupId: Long,
        @PathVariable pollId: Long,
        @RequestParam pollOptionId: Long,
    ): ResponseEntity<Unit> {
        TODO("투표하기 API 구현")
    }

    @Operation(summary = "투표 종료", description = "투표를 종료합니다")
    @PostMapping("/{pollId}/close")
    fun close(
        @PathVariable groupId: Long,
        @PathVariable pollId: Long,
    ): ResponseEntity<Unit> {
        TODO("투표 종료 API 구현")
    }

    @Operation(summary = "투표 삭제", description = "투표를 삭제합니다")
    @DeleteMapping("/{pollId}")
    fun delete(
        @PathVariable groupId: Long,
        @PathVariable pollId: Long,
    ): ResponseEntity<Unit> {
        TODO("투표 삭제 API 구현")
    }
}