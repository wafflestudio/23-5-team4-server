package com.wafflestudio.spring2025.domain.user.member.dto.core

import com.wafflestudio.spring2025.domain.user.member.model.Member
import com.wafflestudio.spring2025.domain.user.member.model.MemberType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "동아리 멤버 정보")
data class MemberDto(
    @Schema(description = "멤버 ID")
    val id: Long,

    @Schema(description = "사용자 ID")
    val userId: Long,

    @Schema(description = "동아리 ID")
    val groupId: Long,

    @Schema(description = "동아리 내 닉네임")
    val nickname: String,

    @Schema(description = "멤버 역할", example = "MEMBER")
    val type: MemberType,
) {
    constructor(member: Member) : this(
        id = member.id!!,
        userId = member.userId,
        groupId = member.groupId,
        nickname = member.nickname,
        type = member.type,
    )
}