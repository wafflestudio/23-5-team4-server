package com.wafflestudio.spring2025.common.email.outbox.model

enum class EmailOutboxEventType {
    REGISTRATION_STATUS,
    REGISTRATION_DELETE,
    WAITLIST_PROMOTION,
    REGISTRATION_DEMOTION,
    EVENT_CANCELLATION,
}
