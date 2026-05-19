package com.wafflestudio.spring2025.domain.event.dto.response

import java.time.Instant

data class EventDetailResponse(
    val event: EventInfo,
    val creator: CreatorInfo,
    val viewer: ViewerInfo,
    val capabilities: CapabilitiesInfo,
    val guestsPreview: List<GuestPreview>,
)

data class EventInfo(
    val publicId: String,
    val title: String,
    val description: String?,
    val location: String?,
    val startsAt: Instant?,
    val endsAt: Instant?,
    val capacity: Int?,
    val confirmedCount: Int,
    val waitlistCount: Int,
    val registrationStartsAt: Instant?,
    val registrationEndsAt: Instant,
)

data class CreatorInfo(
    val name: String,
    val email: String,
    val profileImage: String?,
)

data class ViewerInfo(
    val status: ViewerStatus,
    val name: String?,
    val waitlistPosition: Int?,
    val registrationPublicId: String?,
    val reservationEmail: String?,
)

data class CapabilitiesInfo(
    val shareLink: Boolean,
    val apply: Boolean,
    val wait: Boolean,
    val cancel: Boolean,
)
