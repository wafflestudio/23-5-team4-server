package com.wafflestudio.spring2025.domain.event.dto.core

import com.wafflestudio.spring2025.domain.event.model.Event

data class EventDto(
    val id: String,
    val title: String,
    val description: String?,
    val location: String?,
    val startsAt: Long?,
    val endsAt: Long?,
    val capacity: Int?,
    val waitlistEnabled: Boolean,
    val registrationStartsAt: Long?,
    val registrationEndsAt: Long,
    val createdBy: Long?,
    val createdAt: Long?,
    val updatedAt: Long?,
) {
    constructor(event: Event) : this(
        id = event.publicId,
        title = event.title,
        description = event.description,
        location = event.location,
        startsAt = event.startsAt?.toEpochMilli(),
        endsAt = event.endsAt?.toEpochMilli(),
        capacity = event.capacity,
        waitlistEnabled = event.waitlistEnabled,
        registrationStartsAt = event.registrationStartsAt?.toEpochMilli(),
        registrationEndsAt = event.registrationEndsAt.toEpochMilli(),
        createdBy = event.createdBy,
        createdAt = event.createdAt?.toEpochMilli(),
        updatedAt = event.updatedAt?.toEpochMilli(),
    )
}
