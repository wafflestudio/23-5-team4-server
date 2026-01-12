package com.wafflestudio.spring2025.domain.notification.announcement.dto.core

import com.wafflestudio.spring2025.domain.notification.announcement.model.Announcement
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "공지사항 정보")
data class AnnouncementDto(
    @Schema(description = "공지사항 ID")
    val id: Long,

    @Schema(description = "동아리 ID")
    val groupId: Long,

    @Schema(description = "공지사항 제목")
    val title: String,

    @Schema(description = "공지사항 내용")
    val content: String,

    @Schema(description = "생성 일시 (epoch milliseconds)")
    val createdAt: Long,

    @Schema(description = "수정 일시 (epoch milliseconds)")
    val modifiedAt: Long,

    @Schema(description = "조회수")
    val viewCount: Int,
) {
    constructor(announcement: Announcement) : this(
        id = announcement.id!!,
        groupId = announcement.groupId,
        title = announcement.title,
        content = announcement.content,
        createdAt = announcement.createdAt!!.toEpochMilli(),
        modifiedAt = announcement.modifiedAt!!.toEpochMilli(),
        viewCount = announcement.viewCount,
    )
}