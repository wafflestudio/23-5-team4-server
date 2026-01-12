package com.wafflestudio.spring2025.domain.poll.dto.core

import com.wafflestudio.spring2025.domain.poll.model.Poll
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "투표 정보")
data class PollDto(
    @Schema(description = "투표 ID")
    val id: Long,

    @Schema(description = "동아리 ID")
    val groupId: Long,

    @Schema(description = "투표 제목")
    val title: String,

    @Schema(description = "투표 마감 시간 (epoch milliseconds)")
    val deadline: Long,

    @Schema(description = "생성 일시 (epoch milliseconds)")
    val createdAt: Long,
) {
    constructor(poll: Poll) : this(
        id = poll.id!!,
        groupId = poll.groupId,
        title = poll.title,
        deadline = poll.deadline.toEpochMilli(),
        createdAt = poll.createdAt!!.toEpochMilli(),
    )
}