package com.wafflestudio.spring2025.group.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "동아리 정보 수정 요청")
data class UpdateGroupRequest(
    @Schema(description = "동아리 이름", example = "와플스튜디오")
    val name: String?,

    @Schema(description = "동아리 설명", example = "서울대학교 개발 동아리")
    val detail: String?,
)
