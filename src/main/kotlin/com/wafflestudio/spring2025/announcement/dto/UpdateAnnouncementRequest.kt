package com.wafflestudio.spring2025.announcement.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "공지사항 수정 요청")
data class UpdateAnnouncementRequest(
    @Schema(description = "공지사항 제목")
    val title: String?,

    @Schema(description = "공지사항 내용")
    val content: String?,
)
