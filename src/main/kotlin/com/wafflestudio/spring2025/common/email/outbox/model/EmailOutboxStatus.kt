package com.wafflestudio.spring2025.common.email.outbox.model

enum class EmailOutboxStatus {
    PENDING,
    PROCESSING,
    SENT,
    FAILED,
}
