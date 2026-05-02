package com.wafflestudio.spring2025.domain.registration.dto.response

import com.wafflestudio.spring2025.domain.event.model.Event
import com.wafflestudio.spring2025.domain.registration.model.Registration
import com.wafflestudio.spring2025.domain.registration.model.RegistrationStatus
import java.time.Instant

data class GetMyRegistrationsResponse(
    val registrations: List<MyRegistrationItem>,
)

data class MyRegistrationItem(
    val publicId: String,
    val title: String,
    val startsAt: Instant?,
    val endsAt: Instant?,
    val registrationStartsAt: Instant?,
    val registrationEndsAt: Instant?,
    val capacity: Int?,
    @Schema(description = "확정 참여자 수")
    val confirmedCount: Int,
    @Schema(description = "대기자 수")
    val waitlistCount: Int,
    @Schema(description = "신청 상태")
    val status: MyRegistrationStatus,
    val waitlistedNum: Int?,
) {
    constructor(
        registration: Registration,
        event: Event,
        confirmedCount: Int,
        waitlistCount: Int,
        waitlistedNum: Int?,
    ) : this(
        publicId = event.publicId,
        title = event.title,
        startsAt = event.startsAt,
        endsAt = event.endsAt,
        registrationStartsAt = event.registrationStartsAt,
        registrationEndsAt = event.registrationEndsAt,
        capacity = event.capacity,
        confirmedCount = confirmedCount,
        waitlistCount = waitlistCount,
        status = MyRegistrationStatus.from(registration.status),
        waitlistedNum = waitlistedNum,
    )
}

enum class MyRegistrationStatus {
    CONFIRMED,
    WAITLISTED,
    BANNED,
    ;

    companion object {
        fun from(status: RegistrationStatus): MyRegistrationStatus =
            when (status) {
                RegistrationStatus.HOST,
                RegistrationStatus.CONFIRMED,
                -> CONFIRMED

                RegistrationStatus.WAITLISTED -> WAITLISTED

                RegistrationStatus.BANNED -> BANNED
            }
    }
}
