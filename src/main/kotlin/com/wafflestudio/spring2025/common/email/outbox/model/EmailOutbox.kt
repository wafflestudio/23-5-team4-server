package com.wafflestudio.spring2025.common.email.outbox.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("email_outbox")
class EmailOutbox(
    @Id var id: Long? = null,
    var messageKey: String,
    var eventType: EmailOutboxEventType,
    var recipientEmail: String,
    @Column("payload_json")
    var payloadJson: String,
    var status: EmailOutboxStatus = EmailOutboxStatus.PENDING,
    var retryCount: Int = 0,
    var maxRetryCount: Int = 5,
    var nextRetryAt: Instant = Instant.now(),
    var lastError: String? = null,
    var sentAt: Instant? = null,
    @CreatedDate
    var createdAt: Instant? = null,
    var updatedAt: Instant? = null,
)
