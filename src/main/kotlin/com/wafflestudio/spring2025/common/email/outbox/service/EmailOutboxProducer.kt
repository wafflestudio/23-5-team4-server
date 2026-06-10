package com.wafflestudio.spring2025.common.email.outbox.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.spring2025.common.email.outbox.event.EmailOutboxCreatedEvent
import com.wafflestudio.spring2025.common.email.outbox.model.EmailOutbox
import com.wafflestudio.spring2025.common.email.outbox.model.EmailOutboxEventType
import com.wafflestudio.spring2025.common.email.outbox.repository.EmailOutboxRepository
import com.wafflestudio.spring2025.common.email.service.EmailService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.sql.SQLIntegrityConstraintViolationException
import java.time.Instant

@Component
class EmailOutboxProducer(
    private val emailOutboxRepository: EmailOutboxRepository,
    private val objectMapper: ObjectMapper,
    private val eventPublisher: ApplicationEventPublisher,
    @Value("\${email.outbox.max-retry:5}")
    private val maxRetryCount: Int,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun enqueueRegistrationStatus(data: EmailService.RegistrationStatusEmailData) {
        val registrationPublicId = data.registrationPublicId ?: return
        saveOutbox(
            eventType = EmailOutboxEventType.REGISTRATION_STATUS,
            messageKey = "$registrationPublicId:${data.status.name}",
            recipientEmail = data.toEmail,
            payload = data,
        )
    }

    fun enqueueRegistrationDelete(data: EmailService.RegistrationDeleteEmailData) {
        saveOutbox(
            eventType = EmailOutboxEventType.REGISTRATION_DELETE,
            messageKey = "${data.registrationPublicId}:DELETE",
            recipientEmail = data.toEmail,
            payload = data,
        )
    }

    fun enqueueWaitlistPromotion(data: EmailService.WaitlistPromotionEmailData) {
        saveOutbox(
            eventType = EmailOutboxEventType.WAITLIST_PROMOTION,
            messageKey = "${data.registrationPublicId}:PROMOTION",
            recipientEmail = data.toEmail,
            payload = data,
        )
    }

    fun enqueueRegistrationDemotion(data: EmailService.DemotionEmailData) {
        saveOutbox(
            eventType = EmailOutboxEventType.REGISTRATION_DEMOTION,
            messageKey = "${data.registrationPublicId}:DEMOTION",
            recipientEmail = data.toEmail,
            payload = data,
        )
    }

    fun enqueueEventCancellation(data: EmailService.EventCancellationEmailData) {
        saveOutbox(
            eventType = EmailOutboxEventType.EVENT_CANCELLATION,
            messageKey = "${data.eventPublicId}:${data.toEmail}:EVENT_CANCELLATION",
            recipientEmail = data.toEmail,
            payload = data,
        )
    }

    private fun saveOutbox(
        eventType: EmailOutboxEventType,
        messageKey: String,
        recipientEmail: String,
        payload: Any,
    ) {
        val payloadJson = objectMapper.writeValueAsString(payload)
        val normalizedMessageKey = "${eventType.name}:${sha256(messageKey)}"
        val now = Instant.now()
        val outbox =
            EmailOutbox(
                messageKey = normalizedMessageKey,
                eventType = eventType,
                recipientEmail = recipientEmail,
                payloadJson = payloadJson,
                maxRetryCount = maxRetryCount,
                nextRetryAt = now,
                createdAt = now,
                updatedAt = now,
            )

        try {
            val saved = emailOutboxRepository.save(outbox)
            saved.id?.let { id ->
                eventPublisher.publishEvent(EmailOutboxCreatedEvent(id))
            }
        } catch (ex: Exception) {
            if (isDuplicateOutboxException(ex)) {
                logger.info("중복 outbox 메시지 스킵: {}", normalizedMessageKey)
            } else {
                throw ex
            }
        }
    }

    private fun isDuplicateOutboxException(ex: Throwable): Boolean {
        var current: Throwable? = ex
        while (current != null) {
            if (current is DuplicateKeyException ||
                current is DataIntegrityViolationException ||
                current is SQLIntegrityConstraintViolationException
            ) {
                return true
            }
            current = current.cause
        }
        return false
    }

    private fun sha256(source: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(source.toByteArray(StandardCharsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
