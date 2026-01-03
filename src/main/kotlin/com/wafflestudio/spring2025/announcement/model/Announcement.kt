package com.wafflestudio.spring2025.announcement.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("announcements")
class Announcement(
    @Id var id: Long? = null,
    var groupId: Long,
    var title: String,
    var content: String,
    @CreatedDate
    var createdAt: Instant? = null,
    @LastModifiedDate
    var modifiedAt: Instant? = null,
    var viewCount: Int = 0,
)
