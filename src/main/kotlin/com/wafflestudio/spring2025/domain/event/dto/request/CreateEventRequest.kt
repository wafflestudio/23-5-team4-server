package com.wafflestudio.spring2025.domain.event.dto.request

import java.time.Instant

data class CreateEventRequest(
    val title: String,
    val description: String? = null,
    val location: String? = null,
    val startsAt: Instant? = null,
    val endsAt: Instant? = null,
    val capacity: Int,
    val waitlistEnabled: Boolean,
    val registrationStartsAt: Instant? = null,
    val registrationEndsAt: Instant,
)
