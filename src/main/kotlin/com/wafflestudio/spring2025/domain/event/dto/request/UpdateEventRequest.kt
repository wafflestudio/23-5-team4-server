package com.wafflestudio.spring2025.domain.event.dto.request

import java.time.Instant

data class UpdateEventRequest(
    val title: String? = null,
    val description: String? = null,
    val location: String? = null,
    val startsAt: Instant? = null,
    val endsAt: Instant? = null,
    val capacity: Int? = null,
    val waitlistEnabled: Boolean? = null,
    val registrationStartsAt: Instant? = null,
    val registrationEndsAt: Instant? = null,
)
