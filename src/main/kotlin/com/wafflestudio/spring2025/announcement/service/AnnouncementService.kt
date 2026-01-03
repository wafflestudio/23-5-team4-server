package com.wafflestudio.spring2025.announcement.service

import com.wafflestudio.spring2025.announcement.dto.core.AnnouncementDto
import com.wafflestudio.spring2025.announcement.repository.AnnouncementRepository
import org.springframework.stereotype.Service

@Service
class AnnouncementService(
    private val announcementRepository: AnnouncementRepository,
) {
    fun create(groupId: Long, title: String, content: String): AnnouncementDto {
        TODO("공지사항 생성 구현")
    }

    fun getById(announcementId: Long): AnnouncementDto {
        TODO("공지사항 조회 (조회수 증가) 구현")
    }

    fun getByGroupId(groupId: Long): List<AnnouncementDto> {
        TODO("동아리별 공지사항 목록 조회 (최신순) 구현")
    }

    fun getRecentAnnouncements(groupId: Long, limit: Int = 3): List<AnnouncementDto> {
        TODO("최근 공지사항 조회 구현")
    }

    fun update(announcementId: Long, title: String?, content: String?): AnnouncementDto {
        TODO("공지사항 수정 구현")
    }

    fun delete(announcementId: Long) {
        TODO("공지사항 삭제 구현")
    }
}
