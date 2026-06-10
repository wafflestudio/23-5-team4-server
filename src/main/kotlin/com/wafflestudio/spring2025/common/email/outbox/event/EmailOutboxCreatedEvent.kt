package com.wafflestudio.spring2025.common.email.outbox.event

data class EmailOutboxCreatedEvent(
    val outboxId: Long,
)
