package com.wafflestudio.spring2025.eventuser.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("event_user")
class EventUser(
    @Id var id: Long? = null,
    var userId: Long,
    var eventId: Long,
    var status: EventUserStatus,
    var waitingNumber: Int? = null,
)
