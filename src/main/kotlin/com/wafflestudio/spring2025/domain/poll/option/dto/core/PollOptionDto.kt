package com.wafflestudio.spring2025.domain.poll.option.dto.core

import com.wafflestudio.spring2025.domain.poll.option.model.PollOption
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "투표 선택지 정보")
data class PollOptionDto(
    @Schema(description = "선택지 ID")
    val id: Long,

    @Schema(description = "투표 ID")
    val pollId: Long,

    @Schema(description = "선택지 내용")
    val content: String,
) {
    constructor(pollOption: PollOption) : this(
        id = pollOption.id!!,
        pollId = pollOption.pollId,
        content = pollOption.content,
    )
}