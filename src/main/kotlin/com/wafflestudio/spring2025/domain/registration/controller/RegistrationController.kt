package com.wafflestudio.spring2025.domain.registration.controller

import com.wafflestudio.spring2025.domain.registration.dto.core.RegistrationDto
import com.wafflestudio.spring2025.domain.registration.service.RegistrationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/groups/{groupId}/registrations")
@Tag(name = "Registration", description = "동아리 가입 신청 관리 API")
class RegistrationController(
    private val registrationService: RegistrationService,
) {
    @Operation(summary = "가입 신청 생성", description = "동아리에 가입 신청을 합니다")
    @PostMapping
    fun create(@PathVariable groupId: Long): ResponseEntity<RegistrationDto> {
        TODO("가입 신청 생성 API 구현")
    }

    @Operation(summary = "가입 신청 목록 조회", description = "동아리의 가입 신청 목록을 조회합니다")
    @GetMapping
    fun list(@PathVariable groupId: Long): ResponseEntity<List<RegistrationDto>> {
        TODO("가입 신청 목록 조회 API 구현")
    }

    @Operation(summary = "가입 신청 승인", description = "가입 신청을 승인합니다")
    @PostMapping("/{registrationId}/approve")
    fun approve(
        @PathVariable groupId: Long,
        @PathVariable registrationId: Long,
    ): ResponseEntity<Unit> {
        TODO("가입 신청 승인 API 구현")
    }

    @Operation(summary = "가입 신청 거절", description = "가입 신청을 거절합니다")
    @DeleteMapping("/{registrationId}")
    fun reject(
        @PathVariable groupId: Long,
        @PathVariable registrationId: Long,
    ): ResponseEntity<Unit> {
        TODO("가입 신청 거절 API 구현")
    }
}