package com.wafflestudio.spring2025.group.dto

import com.wafflestudio.spring2025.group.dto.core.GroupDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "동아리 목록 응답")
data class ListGroupResponse(
    @Schema(description = "동아리 목록")
    val groups: List<GroupDto>,
)
