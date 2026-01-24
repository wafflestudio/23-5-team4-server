package com.wafflestudio.spring2025.domain.event.dto.response

import com.wafflestudio.spring2025.domain.event.model.Event
import java.time.Instant

data class UpdateEventResponse(
    val title: String,
    val description: String?,
    val location: String?,
    val startAt: Instant?,
    val endAt: Instant?,
    val capacity: Int?,
    val waitlistEnabled: Boolean,
    val registrationStart: Instant?,
    val registrationDeadline: Instant?,
) {
    companion object {
        fun from(event: Event): UpdateEventResponse =
            UpdateEventResponse(
                title = event.title,
                description = event.description,
                location = event.location,
                startAt = event.startAt,
                endAt = event.endAt,
                capacity = event.capacity,
                waitlistEnabled = event.waitlistEnabled,
                registrationStart = event.registrationStart,
                registrationDeadline = event.registrationDeadline,
            )
    }
}
