package com.wafflestudio.spring2025.group.dto

import com.wafflestudio.spring2025.group.dto.core.GroupDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "동아리 생성 응답")
data class CreateGroupResponse(
    @Schema(description = "생성된 동아리 정보")
    val group: GroupDto,
)
