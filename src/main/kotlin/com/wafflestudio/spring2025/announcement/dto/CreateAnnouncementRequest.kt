package com.wafflestudio.spring2025.announcement.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "공지사항 생성 요청")
data class CreateAnnouncementRequest(
    @Schema(description = "공지사항 제목", example = "정기 모임 안내")
    val title: String,

    @Schema(description = "공지사항 내용", example = "이번 주 금요일 오후 7시에 정기 모임이 있습니다.")
    val content: String,
)
