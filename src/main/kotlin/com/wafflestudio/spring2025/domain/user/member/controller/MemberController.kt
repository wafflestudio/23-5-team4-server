package com.wafflestudio.spring2025.domain.user.member.controller

import com.wafflestudio.spring2025.domain.user.member.dto.ListMemberResponse
import com.wafflestudio.spring2025.domain.user.member.dto.UpdateMemberRequest
import com.wafflestudio.spring2025.domain.user.member.dto.core.MemberDto
import com.wafflestudio.spring2025.domain.user.member.service.MemberService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/groups/{groupId}/members")
@Tag(name = "Member", description = "동아리 멤버 관리 API")
class MemberController(
    private val memberService: MemberService,
) {
    @Operation(summary = "동아리 멤버 목록 조회", description = "동아리의 모든 멤버를 조회합니다")
    @GetMapping
    fun list(@PathVariable groupId: Long): ResponseEntity<ListMemberResponse> {
        TODO("동아리 멤버 목록 조회 API 구현")
    }

    @Operation(summary = "멤버 정보 수정", description = "동아리 멤버의 정보를 수정합니다")
    @PutMapping("/{memberId}")
    fun update(
        @PathVariable groupId: Long,
        @PathVariable memberId: Long,
        @RequestBody request: UpdateMemberRequest,
    ): ResponseEntity<MemberDto> {
        TODO("멤버 정보 수정 API 구현")
    }

    @Operation(summary = "멤버 삭제", description = "동아리에서 멤버를 제거합니다")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "멤버 삭제 성공"),
            ApiResponse(responseCode = "403", description = "권한 없음"),
            ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없음"),
        ],
    )
    @DeleteMapping("/{memberId}")
    fun delete(
        @PathVariable groupId: Long,
        @PathVariable memberId: Long,
    ): ResponseEntity<Unit> {
        TODO("멤버 삭제 API 구현")
    }
}