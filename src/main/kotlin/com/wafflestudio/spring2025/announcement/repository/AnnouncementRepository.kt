package com.wafflestudio.spring2025.announcement.repository

import com.wafflestudio.spring2025.announcement.model.Announcement
import org.springframework.data.repository.ListCrudRepository

interface AnnouncementRepository : ListCrudRepository<Announcement, Long> {
    fun findByGroupIdOrderByCreatedAtDesc(groupId: Long): List<Announcement>
    fun findTop3ByGroupIdOrderByCreatedAtDesc(groupId: Long): List<Announcement>
}
