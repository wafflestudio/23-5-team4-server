package com.wafflestudio.spring2025.member.dto

import com.wafflestudio.spring2025.member.dto.core.MemberDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "동아리 멤버 목록 응답")
data class ListMemberResponse(
    @Schema(description = "멤버 목록")
    val members: List<MemberDto>,
)
