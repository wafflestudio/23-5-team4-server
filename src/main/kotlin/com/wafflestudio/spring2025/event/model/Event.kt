package com.wafflestudio.spring2025.event.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("events")
class Event(
    @Id var id: Long? = null,
    var groupId: Long,
    var startTime: Instant,
    var endTime: Instant,
    var registerDeadline: Instant,
    var detail: String,
    var capacity: Int,
)
