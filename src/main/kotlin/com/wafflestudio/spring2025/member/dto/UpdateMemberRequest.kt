package com.wafflestudio.spring2025.member.dto

import com.wafflestudio.spring2025.member.model.MemberType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "멤버 정보 수정 요청")
data class UpdateMemberRequest(
    @Schema(description = "동아리 내 닉네임", example = "홍길동")
    val nickname: String?,

    @Schema(description = "멤버 역할", example = "ADMIN")
    val type: MemberType?,
)
