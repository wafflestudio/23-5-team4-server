package com.wafflestudio.spring2025.common.email.outbox.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.spring2025.common.email.outbox.event.EmailOutboxCreatedEvent
import com.wafflestudio.spring2025.common.email.outbox.model.EmailOutbox
import com.wafflestudio.spring2025.common.email.outbox.model.EmailOutboxEventType
import com.wafflestudio.spring2025.common.email.outbox.repository.EmailOutboxCommandRepository
import com.wafflestudio.spring2025.common.email.outbox.repository.EmailOutboxRepository
import com.wafflestudio.spring2025.common.email.service.EmailService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.Instant

@Component
@ConditionalOnProperty(prefix = "email.outbox", name = ["enabled"], havingValue = "true", matchIfMissing = true)
class EmailOutboxWorker(
    private val emailOutboxRepository: EmailOutboxRepository,
    private val emailOutboxCommandRepository: EmailOutboxCommandRepository,
    private val emailService: EmailService,
    private val objectMapper: ObjectMapper,
    @Value("\${email.outbox.batch-size:50}")
    private val batchSize: Int,
    @Value("\${email.outbox.backoff-seconds:30}")
    private val backoffSeconds: Long,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onEmailOutboxCreated(event: EmailOutboxCreatedEvent) {
        processEmail(event.outboxId)
    }

    @Scheduled(fixedDelayString = "\${email.outbox.poll-interval-ms:1000}")
    fun processPendingEmails() {
        val now = Instant.now()
        val targetIds = emailOutboxCommandRepository.findProcessableIds(batchSize.coerceAtLeast(1), now)
        targetIds.forEach { id ->
            processEmail(id)
        }
    }

    private fun processEmail(id: Long) {
        val now = Instant.now()
        if (!emailOutboxCommandRepository.claimForProcessing(id, now)) return
        val message = emailOutboxRepository.findById(id).orElse(null) ?: return
        runCatching { dispatch(message) }
            .onSuccess {
                emailOutboxCommandRepository.markSent(id)
            }.onFailure { throwable ->
                handleFailure(message, throwable)
            }
    }

    private fun dispatch(message: EmailOutbox) {
        when (message.eventType) {
            EmailOutboxEventType.REGISTRATION_STATUS -> {
                val payload =
                    objectMapper.readValue(
                        message.payloadJson,
                        EmailService.RegistrationStatusEmailData::class.java,
                    )
                emailService.sendRegistrationStatusEmail(payload)
            }

            EmailOutboxEventType.REGISTRATION_DELETE -> {
                val payload =
                    objectMapper.readValue(
                        message.payloadJson,
                        EmailService.RegistrationDeleteEmailData::class.java,
                    )
                emailService.sendRegistrationDeleteEmail(payload)
            }

            EmailOutboxEventType.WAITLIST_PROMOTION -> {
                val payload =
                    objectMapper.readValue(
                        message.payloadJson,
                        EmailService.WaitlistPromotionEmailData::class.java,
                    )
                emailService.sendWaitlistPromotionEmail(payload)
            }

            EmailOutboxEventType.REGISTRATION_DEMOTION -> {
                val payload =
                    objectMapper.readValue(
                        message.payloadJson,
                        EmailService.DemotionEmailData::class.java,
                    )
                emailService.sendDemotionEmail(payload)
            }

            EmailOutboxEventType.EVENT_CANCELLATION -> {
                val payload =
                    objectMapper.readValue(
                        message.payloadJson,
                        EmailService.EventCancellationEmailData::class.java,
                    )
                emailService.sendEventCancellationEmail(payload)
            }
        }
    }

    private fun handleFailure(
        message: EmailOutbox,
        throwable: Throwable,
    ) {
        val id = message.id ?: return
        val nextRetryCount = message.retryCount + 1
        val exponent = (nextRetryCount - 1).coerceAtMost(10)
        val nextRetryAt = Instant.now().plusSeconds(backoffSeconds * (1L shl exponent))
        val reason = truncateError(throwable.message ?: throwable.javaClass.simpleName)
        emailOutboxCommandRepository.markFailed(
            id = id,
            retryCount = nextRetryCount,
            nextRetryAt = nextRetryAt,
            lastError = reason,
        )
        logger.warn(
            "이메일 outbox 처리 실패 id={}, eventType={}, retryCount={}/{} reason={}",
            id,
            message.eventType,
            nextRetryCount,
            message.maxRetryCount,
            reason,
        )
    }

    private fun truncateError(message: String): String =
        if (message.length <= 1000) {
            message
        } else {
            message.substring(0, 1000)
        }
}
