package com.wafflestudio.spring2025.domain.group.dto.core

import com.wafflestudio.spring2025.domain.group.model.Group
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "동아리 정보")
data class GroupDto(
    @Schema(description = "동아리 ID")
    val id: Long,

    @Schema(description = "동아리 이름")
    val name: String,

    @Schema(description = "동아리 초대 코드")
    val code: String,

    @Schema(description = "동아리 설명")
    val detail: String,
) {
    constructor(group: Group) : this(
        id = group.id!!,
        name = group.name,
        code = group.code,
        detail = group.detail,
    )
}