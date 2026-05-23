package com.wafflestudio.spring2025.domain.registration.dto.core

import com.wafflestudio.spring2025.domain.event.model.Event
import com.wafflestudio.spring2025.domain.registration.model.Registration

data class RegistrationWithEventDto(
    val registration: RegistrationDto,
    val event: EventSummaryDto,
) {
    constructor(registration: Registration, event: Event) : this(
        registration = RegistrationDto(registration),
        event = EventSummaryDto(event),
    )
}

data class EventSummaryDto(
    val id: Long,
    val title: String,
    val location: String?,
    val startsAt: Long?,
    val endsAt: Long?,
) {
    constructor(event: Event) : this(
        id = event.id!!,
        title = event.title,
        location = event.location,
        startsAt = event.startsAt?.toEpochMilli(),
        endsAt = event.endsAt?.toEpochMilli(),
    )
}
