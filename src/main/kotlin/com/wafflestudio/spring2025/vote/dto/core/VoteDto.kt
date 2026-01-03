package com.wafflestudio.spring2025.vote.dto.core

import com.wafflestudio.spring2025.vote.model.Vote
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "투표 기록 정보")
data class VoteDto(
    @Schema(description = "투표 기록 ID")
    val id: Long,

    @Schema(description = "선택지 ID")
    val pollOptionId: Long,

    @Schema(description = "멤버 ID")
    val memberId: Long,
) {
    constructor(vote: Vote) : this(
        id = vote.id!!,
        pollOptionId = vote.pollOptionId,
        memberId = vote.memberId,
    )
}
