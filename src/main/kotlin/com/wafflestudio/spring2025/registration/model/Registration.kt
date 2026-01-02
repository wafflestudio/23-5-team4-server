package com.wafflestudio.spring2025.registration.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("registrations")
class Registration(
    @Id var id: Long? = null,
    var userId: Long,
    var groupId: Long,
    @CreatedDate
    var createdAt: Instant? = null,
)
