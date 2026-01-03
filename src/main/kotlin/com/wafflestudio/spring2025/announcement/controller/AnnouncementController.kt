package com.wafflestudio.spring2025.announcement.controller

import com.wafflestudio.spring2025.announcement.dto.CreateAnnouncementRequest
import com.wafflestudio.spring2025.announcement.dto.UpdateAnnouncementRequest
import com.wafflestudio.spring2025.announcement.dto.core.AnnouncementDto
import com.wafflestudio.spring2025.announcement.service.AnnouncementService
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
@RequestMapping("/api/v1/groups/{groupId}/announcements")
@Tag(name = "Announcement", description = "공지사항 관리 API")
class AnnouncementController(
    private val announcementService: AnnouncementService,
) {
    @Operation(summary = "공지사항 생성", description = "새로운 공지사항을 생성합니다")
    @PostMapping
    fun create(
        @PathVariable groupId: Long,
        @RequestBody request: CreateAnnouncementRequest,
    ): ResponseEntity<AnnouncementDto> {
        TODO("공지사항 생성 API 구현")
    }

    @Operation(summary = "공지사항 목록 조회", description = "동아리의 모든 공지사항을 조회합니다")
    @GetMapping
    fun list(@PathVariable groupId: Long): ResponseEntity<List<AnnouncementDto>> {
        TODO("공지사항 목록 조회 API 구현")
    }

    @Operation(summary = "공지사항 상세 조회", description = "공지사항 상세 정보를 조회합니다")
    @GetMapping("/{announcementId}")
    fun getById(
        @PathVariable groupId: Long,
        @PathVariable announcementId: Long,
    ): ResponseEntity<AnnouncementDto> {
        TODO("공지사항 상세 조회 API 구현")
    }

    @Operation(summary = "공지사항 수정", description = "공지사항을 수정합니다")
    @PutMapping("/{announcementId}")
    fun update(
        @PathVariable groupId: Long,
        @PathVariable announcementId: Long,
        @RequestBody request: UpdateAnnouncementRequest,
    ): ResponseEntity<AnnouncementDto> {
        TODO("공지사항 수정 API 구현")
    }

    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다")
    @DeleteMapping("/{announcementId}")
    fun delete(
        @PathVariable groupId: Long,
        @PathVariable announcementId: Long,
    ): ResponseEntity<Unit> {
        TODO("공지사항 삭제 API 구현")
    }
}
