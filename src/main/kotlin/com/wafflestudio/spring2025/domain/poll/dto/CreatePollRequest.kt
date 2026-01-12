package com.wafflestudio.spring2025.domain.poll.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "투표 생성 요청")
data class CreatePollRequest(
    @Schema(description = "투표 제목", example = "다음 MT 장소 투표")
    val title: String,

    @Schema(description = "투표 마감 시간 (epoch milliseconds)", example = "1700000000000")
    val deadline: Long,

    @Schema(description = "투표 선택지 목록", example = "[\"강원도\", \"제주도\", \"부산\"]")
    val options: List<String>,
)