package com.wafflestudio.spring2025.poll.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("polls")
class Poll(
    @Id var id: Long? = null,
    var groupId: Long,
    var title: String,
    var deadline: Instant,
    @CreatedDate
    var createdAt: Instant? = null,
)
