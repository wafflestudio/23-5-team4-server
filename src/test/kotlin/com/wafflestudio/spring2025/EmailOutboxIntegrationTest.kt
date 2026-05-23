package com.wafflestudio.spring2025

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.spring2025.TestContainerConfig
import com.wafflestudio.spring2025.common.email.outbox.model.EmailOutbox
import com.wafflestudio.spring2025.common.email.outbox.model.EmailOutboxEventType
import com.wafflestudio.spring2025.common.email.outbox.model.EmailOutboxStatus
import com.wafflestudio.spring2025.common.email.outbox.repository.EmailOutboxCommandRepository
import com.wafflestudio.spring2025.common.email.outbox.repository.EmailOutboxRepository
import com.wafflestudio.spring2025.common.email.outbox.service.EmailOutboxProducer
import com.wafflestudio.spring2025.common.email.outbox.service.EmailOutboxWorker
import com.wafflestudio.spring2025.common.email.service.EmailService
import com.wafflestudio.spring2025.domain.registration.model.RegistrationStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant
import java.util.UUID

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Import(TestContainerConfig::class)
class EmailOutboxIntegrationTest
    @Autowired
    constructor(
        private val producer: EmailOutboxProducer,
        private val worker: EmailOutboxWorker,
        private val emailOutboxRepository: EmailOutboxRepository,
        private val emailOutboxCommandRepository: EmailOutboxCommandRepository,
        private val mapper: ObjectMapper,
    ) {
        @MockitoBean
        private lateinit var emailService: EmailService

        @AfterEach
        fun cleanup() {
            emailOutboxRepository.deleteAll()
        }

        @Test
        fun `등록 상태는 멱등한 키로 아웃박스에 적재된다`() {
            val data =
                EmailService.RegistrationStatusEmailData(
                    toEmail = "user@example.com",
                    status = RegistrationStatus.WAITLISTED,
                    name = "참여자",
                    eventTitle = "행사",
                    startsAt = Instant.parse("2026-01-01T00:00:00Z"),
                    endsAt = Instant.parse("2026-01-01T02:00:00Z"),
                    location = "회의실",
                    totalCount = 1,
                    capacity = 10,
                    registrationStartsAt = Instant.parse("2025-12-31T23:00:00Z"),
                    registrationEndsAt = Instant.parse("2025-12-31T23:50:00Z"),
                    description = "소개",
                    publicId = "event-1",
                    registrationPublicId = "reg-1",
                )

            producer.enqueueRegistrationStatus(data)
            producer.enqueueRegistrationStatus(data)

            val rows = emailOutboxRepository.findAll().toList()
            assertThat(rows).hasSize(1)
            assertThat(rows[0].messageKey).startsWith("REGISTRATION_STATUS:")
            assertThat(rows[0].messageKey).hasSizeGreaterThan("REGISTRATION_STATUS:".length)
        }

        @Test
        fun `이벤트 타입별 메시지 키는 이벤트 타입으로 시작한다`() {
            val now = Instant.parse("2026-01-01T00:00:00Z")

            val registrationStatusData =
                EmailService.RegistrationStatusEmailData(
                    toEmail = "user-status@example.com",
                    status = RegistrationStatus.WAITLISTED,
                    name = "참여자",
                    eventTitle = "행사",
                    startsAt = now,
                    endsAt = now.plusSeconds(7200),
                    location = "회의실",
                    totalCount = 1,
                    capacity = 10,
                    registrationStartsAt = now.minusSeconds(3600),
                    registrationEndsAt = now.minusSeconds(3000),
                    description = "소개",
                    publicId = "event-1",
                    registrationPublicId = "reg-status",
                )
            val registrationDeleteData =
                EmailService.RegistrationDeleteEmailData(
                    toEmail = "user-delete@example.com",
                    registrationPublicId = "reg-delete",
                    name = "참여자",
                    eventTitle = "행사",
                    startsAt = now,
                    endsAt = now.plusSeconds(7200),
                    location = "회의실",
                    totalCount = 1,
                    capacity = 10,
                    registrationStartsAt = now.minusSeconds(3600),
                    registrationEndsAt = now.minusSeconds(3000),
                    description = "소개",
                )
            val promotionData =
                EmailService.WaitlistPromotionEmailData(
                    toEmail = "user-promotion@example.com",
                    eventTitle = "행사",
                    name = "참여자",
                    waitingNum = 1,
                    startsAt = now,
                    endsAt = now.plusSeconds(7200),
                    location = "회의실",
                    totalCount = 1,
                    capacity = 10,
                    registrationStartsAt = now.minusSeconds(3600),
                    registrationEndsAt = now.minusSeconds(3000),
                    description = "소개",
                    eventPublicId = "event-1",
                    registrationPublicId = "reg-promotion",
                )
            val demotionData =
                EmailService.DemotionEmailData(
                    toEmail = "user-demotion@example.com",
                    name = "참여자",
                    eventTitle = "행사",
                    startsAt = now,
                    endsAt = now.plusSeconds(7200),
                    location = "회의실",
                    newCapacity = 2,
                    registrationStartsAt = now.minusSeconds(3600),
                    registrationEndsAt = now.minusSeconds(3000),
                    description = "소개",
                    publicId = "event-1",
                    registrationPublicId = "reg-demotion",
                    waitingNum = 2,
                )
            val cancellationData =
                EmailService.EventCancellationEmailData(
                    toEmail = "user-cancel@example.com",
                    eventPublicId = "event-1",
                    name = "참여자",
                    eventTitle = "행사",
                    startsAt = now,
                    endsAt = now.plusSeconds(7200),
                    location = "회의실",
                    description = "소개",
                    hostEmail = "host@example.com",
                )

            producer.enqueueRegistrationStatus(registrationStatusData)
            producer.enqueueRegistrationDelete(registrationDeleteData)
            producer.enqueueWaitlistPromotion(promotionData)
            producer.enqueueRegistrationDemotion(demotionData)
            producer.enqueueEventCancellation(cancellationData)

            val savedByType =
                emailOutboxRepository
                    .findAll()
                    .associateBy { it.eventType }

            assertThat(savedByType[EmailOutboxEventType.REGISTRATION_STATUS]!!.messageKey).startsWith("REGISTRATION_STATUS:")
            assertThat(savedByType[EmailOutboxEventType.REGISTRATION_DELETE]!!.messageKey).startsWith("REGISTRATION_DELETE:")
            assertThat(savedByType[EmailOutboxEventType.WAITLIST_PROMOTION]!!.messageKey).startsWith("WAITLIST_PROMOTION:")
            assertThat(savedByType[EmailOutboxEventType.REGISTRATION_DEMOTION]!!.messageKey).startsWith("REGISTRATION_DEMOTION:")
            assertThat(savedByType[EmailOutboxEventType.EVENT_CANCELLATION]!!.messageKey).startsWith("EVENT_CANCELLATION:")
        }

        @Test
        fun `등록 상태 메시지 키는 상태값이 바뀌면 변경된다`() {
            val base =
                EmailService.RegistrationStatusEmailData(
                    toEmail = "user@example.com",
                    status = RegistrationStatus.CONFIRMED,
                    name = "참여자",
                    eventTitle = "행사",
                    startsAt = Instant.parse("2026-01-01T00:00:00Z"),
                    endsAt = Instant.parse("2026-01-01T02:00:00Z"),
                    location = "회의실",
                    totalCount = 1,
                    capacity = 10,
                    registrationStartsAt = Instant.parse("2025-12-31T23:00:00Z"),
                    registrationEndsAt = Instant.parse("2025-12-31T23:50:00Z"),
                    description = "소개",
                    publicId = "event-1",
                    registrationPublicId = "reg-1",
                )

            producer.enqueueRegistrationStatus(base)
            producer.enqueueRegistrationStatus(base.copy(status = RegistrationStatus.WAITLISTED))

            val keys = emailOutboxRepository.findAll().map { it.messageKey }
            assertThat(keys).hasSize(2)
            assertThat(keys[0]).isNotEqualTo(keys[1])
        }

        @Test
        fun `중복 키는 예외가 아닌 스킵 처리된다`() {
            val data =
                EmailService.RegistrationDeleteEmailData(
                    toEmail = "user@example.com",
                    registrationPublicId = "reg-dup",
                    name = "참여자",
                    eventTitle = "행사",
                    startsAt = Instant.parse("2026-01-01T00:00:00Z"),
                    endsAt = Instant.parse("2026-01-01T02:00:00Z"),
                    location = "회의실",
                    totalCount = 1,
                    capacity = 10,
                    registrationStartsAt = Instant.parse("2025-12-31T23:00:00Z"),
                    registrationEndsAt = Instant.parse("2025-12-31T23:50:00Z"),
                    description = "소개",
                )

            producer.enqueueRegistrationDelete(data)
            producer.enqueueRegistrationDelete(data)

            assertThat(emailOutboxRepository.findAll()).hasSize(1)
        }

        @Test
        fun `registrationPublicId가 없으면 등록 상태 아웃박스는 적재하지 않는다`() {
            val data =
                EmailService.RegistrationStatusEmailData(
                    toEmail = "user@example.com",
                    status = RegistrationStatus.CONFIRMED,
                    name = "참여자",
                    eventTitle = "행사",
                    startsAt = Instant.parse("2026-01-01T00:00:00Z"),
                    endsAt = Instant.parse("2026-01-01T02:00:00Z"),
                    location = "회의실",
                    totalCount = 1,
                    capacity = 10,
                    registrationStartsAt = Instant.parse("2025-12-31T23:00:00Z"),
                    registrationEndsAt = Instant.parse("2025-12-31T23:50:00Z"),
                    description = "소개",
                    publicId = "event-1",
                    registrationPublicId = null,
                )

            producer.enqueueRegistrationStatus(data)

            assertThat(emailOutboxRepository.findAll()).isEmpty()
        }

        @Test
        fun `처리 가능한 아웃박스는 이벤트 타입별로 적절히 디스패치되고 SENT 처리된다`() {
            val now = Instant.parse("2026-01-01T00:00:00Z")
            val registrationStatusData =
                EmailService.RegistrationStatusEmailData(
                    toEmail = "user-1@example.com",
                    status = RegistrationStatus.CONFIRMED,
                    name = "참여자",
                    eventTitle = "행사",
                    startsAt = now,
                    endsAt = now.plusSeconds(7200),
                    location = "회의실",
                    totalCount = 1,
                    capacity = 10,
                    registrationStartsAt = now.minusSeconds(3600),
                    registrationEndsAt = now.minusSeconds(3000),
                    description = "소개",
                    publicId = "event-1",
                    registrationPublicId = "reg-1",
                )
            val registrationDeleteData =
                EmailService.RegistrationDeleteEmailData(
                    toEmail = "user-2@example.com",
                    registrationPublicId = "reg-2",
                    name = "참여자",
                    eventTitle = "행사",
                    startsAt = now,
                    endsAt = now.plusSeconds(7200),
                    location = "회의실",
                    totalCount = 1,
                    capacity = 10,
                    registrationStartsAt = now.minusSeconds(3600),
                    registrationEndsAt = now.minusSeconds(3000),
                    description = "소개",
                )
            val promotionData =
                EmailService.WaitlistPromotionEmailData(
                    toEmail = "user-3@example.com",
                    eventTitle = "행사",
                    name = "참여자",
                    waitingNum = 1,
                    startsAt = now,
                    endsAt = now.plusSeconds(7200),
                    location = "회의실",
                    totalCount = 1,
                    capacity = 10,
                    registrationStartsAt = now.minusSeconds(3600),
                    registrationEndsAt = now.minusSeconds(3000),
                    description = "소개",
                    eventPublicId = "event-1",
                    registrationPublicId = "reg-3",
                )
            val demotionData =
                EmailService.DemotionEmailData(
                    toEmail = "user-4@example.com",
                    name = "참여자",
                    eventTitle = "행사",
                    startsAt = now,
                    endsAt = now.plusSeconds(7200),
                    location = "회의실",
                    newCapacity = 2,
                    registrationStartsAt = now.minusSeconds(3600),
                    registrationEndsAt = now.minusSeconds(3000),
                    description = "소개",
                    publicId = "event-1",
                    registrationPublicId = "reg-4",
                    waitingNum = 2,
                )
            val cancellationData =
                EmailService.EventCancellationEmailData(
                    toEmail = "user-5@example.com",
                    eventPublicId = "event-1",
                    name = "참여자",
                    eventTitle = "행사",
                    startsAt = now,
                    endsAt = now.plusSeconds(7200),
                    location = "회의실",
                    description = "소개",
                    hostEmail = "host@example.com",
                )

            val ids =
                listOf(
                    emailOutboxRepository
                        .save(
                            outbox(EmailOutboxEventType.REGISTRATION_STATUS, registrationStatusData, "m-${UUID.randomUUID()}"),
                        ).id!!,
                    emailOutboxRepository
                        .save(
                            outbox(EmailOutboxEventType.REGISTRATION_DELETE, registrationDeleteData, "m-${UUID.randomUUID()}"),
                        ).id!!,
                    emailOutboxRepository
                        .save(
                            outbox(EmailOutboxEventType.WAITLIST_PROMOTION, promotionData, "m-${UUID.randomUUID()}"),
                        ).id!!,
                    emailOutboxRepository
                        .save(
                            outbox(EmailOutboxEventType.REGISTRATION_DEMOTION, demotionData, "m-${UUID.randomUUID()}"),
                        ).id!!,
                    emailOutboxRepository
                        .save(
                            outbox(EmailOutboxEventType.EVENT_CANCELLATION, cancellationData, "m-${UUID.randomUUID()}"),
                        ).id!!,
                )
            worker.processPendingEmails()

            verify(emailService).sendRegistrationStatusEmail(registrationStatusData)
            verify(emailService).sendRegistrationDeleteEmail(registrationDeleteData)
            verify(emailService).sendWaitlistPromotionEmail(promotionData)
            verify(emailService).sendDemotionEmail(demotionData)
            verify(emailService).sendEventCancellationEmail(cancellationData)

            ids.forEach { id ->
                val outbox = emailOutboxRepository.findById(id).orElseThrow()
                assertThat(outbox.status).isEqualTo(EmailOutboxStatus.SENT)
            }
        }

        @Test
        fun `잘못된 payload는 실패 상태와 retry 정보로 저장된다`() {
            val row =
                emailOutboxRepository.save(
                    outbox(EmailOutboxEventType.REGISTRATION_STATUS, "{}", "broken-${UUID.randomUUID()}"),
                )
            worker.processPendingEmails()

            val after = emailOutboxRepository.findById(row.id!!).orElseThrow()
            assertThat(after.status).isEqualTo(EmailOutboxStatus.FAILED)
            assertThat(after.retryCount).isEqualTo(1)
            assertThat(after.nextRetryAt).isAfter(Instant.now().minusSeconds(1))
            assertThat(after.nextRetryAt).isBefore(Instant.now().plusSeconds(120))
            assertThat(after.lastError).isNotBlank()
        }

        @Test
        fun `이메일 전송 예외는 실패로 처리되고 에러가 기록된다`() {
            val data =
                EmailService.RegistrationStatusEmailData(
                    toEmail = "user@example.com",
                    status = RegistrationStatus.CONFIRMED,
                    name = "참여자",
                    eventTitle = "행사",
                    startsAt = Instant.parse("2026-01-01T00:00:00Z"),
                    endsAt = Instant.parse("2026-01-01T02:00:00Z"),
                    location = "회의실",
                    totalCount = 1,
                    capacity = 10,
                    registrationStartsAt = Instant.parse("2025-12-31T23:00:00Z"),
                    registrationEndsAt = Instant.parse("2025-12-31T23:50:00Z"),
                    description = "소개",
                    publicId = "event-1",
                    registrationPublicId = "reg-1",
                )
            val row =
                emailOutboxRepository.save(
                    outbox(EmailOutboxEventType.REGISTRATION_STATUS, data, "smtp-${UUID.randomUUID()}"),
                )
            whenever(emailService.sendRegistrationStatusEmail(any())).thenThrow(RuntimeException("smtp down"))
            worker.processPendingEmails()

            val after = emailOutboxRepository.findById(row.id!!).orElseThrow()
            assertThat(after.status).isEqualTo(EmailOutboxStatus.FAILED)
            assertThat(after.retryCount).isEqualTo(1)
            assertThat(after.lastError).isEqualTo("smtp down")
            verify(emailService).sendRegistrationStatusEmail(data)
        }

        @Test
        fun `실패 사유는 1000자로 잘린다`() {
            val data =
                EmailService.RegistrationStatusEmailData(
                    toEmail = "user@example.com",
                    status = RegistrationStatus.CONFIRMED,
                    name = "참여자",
                    eventTitle = "행사",
                    startsAt = Instant.parse("2026-01-01T00:00:00Z"),
                    endsAt = Instant.parse("2026-01-01T02:00:00Z"),
                    location = "회의실",
                    totalCount = 1,
                    capacity = 10,
                    registrationStartsAt = Instant.parse("2025-12-31T23:00:00Z"),
                    registrationEndsAt = Instant.parse("2025-12-31T23:50:00Z"),
                    description = "소개",
                    publicId = "event-1",
                    registrationPublicId = "reg-1",
                )
            val row =
                emailOutboxRepository.save(outbox(EmailOutboxEventType.REGISTRATION_STATUS, data, "long-${UUID.randomUUID()}"))

            whenever(emailService.sendRegistrationStatusEmail(any())).thenThrow(RuntimeException("E".repeat(1205)))
            worker.processPendingEmails()

            val after = emailOutboxRepository.findById(row.id!!).orElseThrow()
            assertThat(after.status).isEqualTo(EmailOutboxStatus.FAILED)
            assertThat(after.lastError).hasSize(1000)
            assertThat(after.lastError).isEqualTo("E".repeat(1000))
        }

        @Test
        fun `findProcessableIds는 처리 가능한 상태만 조회한다`() {
            val now = Instant.now()
            val pendingNow =
                emailOutboxRepository.save(
                    outbox(
                        EmailOutboxEventType.REGISTRATION_STATUS,
                        "{\"toEmail\":\"user-1@example.com\"}",
                        "q-${UUID.randomUUID()}",
                        status = EmailOutboxStatus.PENDING,
                        nextRetryAt = now.minusSeconds(60),
                        retryCount = 0,
                        maxRetryCount = 5,
                    ),
                )
            val futurePending =
                emailOutboxRepository.save(
                    outbox(
                        EmailOutboxEventType.REGISTRATION_STATUS,
                        "{\"toEmail\":\"user-2@example.com\"}",
                        "q-${UUID.randomUUID()}",
                        status = EmailOutboxStatus.PENDING,
                        nextRetryAt = now.plusSeconds(60),
                        retryCount = 0,
                        maxRetryCount = 5,
                    ),
                )
            val failedReady =
                emailOutboxRepository.save(
                    outbox(
                        EmailOutboxEventType.REGISTRATION_STATUS,
                        "{\"toEmail\":\"user-3@example.com\"}",
                        "q-${UUID.randomUUID()}",
                        status = EmailOutboxStatus.FAILED,
                        nextRetryAt = now.minusSeconds(60),
                        retryCount = 1,
                        maxRetryCount = 5,
                    ),
                )
            val failedOverRetry =
                emailOutboxRepository.save(
                    outbox(
                        EmailOutboxEventType.REGISTRATION_STATUS,
                        "{\"toEmail\":\"user-4@example.com\"}",
                        "q-${UUID.randomUUID()}",
                        status = EmailOutboxStatus.FAILED,
                        nextRetryAt = now.minusSeconds(60),
                        retryCount = 5,
                        maxRetryCount = 5,
                    ),
                )
            val processing =
                emailOutboxRepository.save(
                    outbox(
                        EmailOutboxEventType.REGISTRATION_STATUS,
                        "{\"toEmail\":\"user-5@example.com\"}",
                        "q-${UUID.randomUUID()}",
                        status = EmailOutboxStatus.PROCESSING,
                        nextRetryAt = now.minusSeconds(60),
                    ),
                )

            val ids = emailOutboxCommandRepository.findProcessableIds(20)

            assertThat(ids).hasSize(2)
            assertThat(ids).containsExactlyInAnyOrder(pendingNow.id, failedReady.id)
            assertThat(ids).doesNotContain(futurePending.id, failedOverRetry.id, processing.id)
        }

        @Test
        fun `처리 대상이 아닌 아웃박스는 worker 처리에서 건너뛴다`() {
            val processing =
                emailOutboxRepository.save(
                    outbox(
                        EmailOutboxEventType.REGISTRATION_STATUS,
                        "{\"toEmail\":\"user@example.com\"}",
                        "skip-${UUID.randomUUID()}",
                        status = EmailOutboxStatus.PROCESSING,
                        nextRetryAt = Instant.now().minusSeconds(10),
                    ),
                )

            worker.processPendingEmails()

            assertThat(emailOutboxRepository.findById(processing.id!!).orElseThrow().status)
                .isEqualTo(EmailOutboxStatus.PROCESSING)
            verifyNoInteractions(emailService)
            verify(emailService, never()).sendRegistrationStatusEmail(any())
        }

        private fun outbox(
            eventType: EmailOutboxEventType,
            payload: Any,
            messageKey: String,
            status: EmailOutboxStatus = EmailOutboxStatus.PENDING,
            nextRetryAt: Instant = Instant.now().minusSeconds(60),
            retryCount: Int = 0,
            maxRetryCount: Int = 5,
            recipientEmail: String = "user-${UUID.randomUUID()}@example.com",
        ): EmailOutbox {
            val now = Instant.now()
            return EmailOutbox(
                messageKey = messageKey,
                eventType = eventType,
                recipientEmail = recipientEmail,
                payloadJson = if (payload is String) payload else mapper.writeValueAsString(payload),
                status = status,
                retryCount = retryCount,
                maxRetryCount = maxRetryCount,
                nextRetryAt = nextRetryAt,
                createdAt = now,
                updatedAt = now,
            )
        }
    }
