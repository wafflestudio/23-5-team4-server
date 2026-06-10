package com.wafflestudio.spring2025.common.email.service

import com.wafflestudio.spring2025.common.email.client.EmailClient
import com.wafflestudio.spring2025.config.EmailConfig
import com.wafflestudio.spring2025.domain.registration.model.RegistrationStatus
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class EmailService(
    private val emailConfig: EmailConfig,
    private val emailClient: EmailClient,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Sends a verification email to the specified email address
     * @param toEmail recipient email address
     * @param verificationCode verification code to include in the URL
     */
    fun sendVerificationEmail(
        toEmail: String,
        verificationCode: String,
    ) {
        val verificationUrl = "${emailConfig.serviceDomain}/auth/verify?verificationCode=$verificationCode"
        val htmlContent =
            loadTemplate("email-verification.html")
                .replace("{verificationUrl}", verificationUrl)

        sendHtmlEmail(
            to = toEmail,
            subject = "모이밍 이메일 인증",
            htmlContent = htmlContent,
        )

        logger.info("Verification email sent to: $toEmail")
    }

    private fun sendHtmlEmail(
        to: String,
        subject: String,
        htmlContent: String,
    ) {
        emailClient.sendEmail(
            to = to,
            subject = subject,
            htmlContent = htmlContent,
            fromEmail = emailConfig.fromEmail,
            fromName = emailConfig.fromName,
        )
    }

    /**
     * Loads an email template from the template directory
     * @param templateName name of the template file
     * @return template content as string
     */
    private fun loadTemplate(templateName: String): String =
        try {
            val resource = ClassPathResource("com/wafflestudio/spring2025/common/email/template/$templateName")
            resource.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
        } catch (e: Exception) {
            logger.error("Failed to load email template: $templateName", e)
            throw IllegalStateException("Email template not found: $templateName", e)
        }

    data class RegistrationStatusEmailData(
        val toEmail: String,
        val status: RegistrationStatus,
        val name: String,
        val eventTitle: String?,
        val startsAt: Instant?,
        val endsAt: Instant?,
        val location: String?,
        val totalCount: Int?,
        val capacity: Int?,
        val registrationStartsAt: Instant?,
        val registrationEndsAt: Instant?,
        val description: String?,
        val publicId: String?,
        val registrationPublicId: String?,
        val waitingNum: Int? = null,
    )

    data class RegistrationDeleteEmailData(
        val toEmail: String,
        val registrationPublicId: String,
        val name: String,
        val eventTitle: String?,
        val startsAt: Instant?,
        val endsAt: Instant?,
        val location: String?,
        val totalCount: Int?,
        val capacity: Int?,
        val registrationStartsAt: Instant?,
        val registrationEndsAt: Instant?,
        val description: String?,
        val publicId: String?,
    )

    data class EventCancellationEmailData(
        val toEmail: String,
        val eventPublicId: String,
        val name: String,
        val eventTitle: String?,
        val startsAt: Instant?,
        val endsAt: Instant?,
        val location: String?,
        val description: String?,
        val hostEmail: String,
    )

    data class DemotionEmailData(
        val toEmail: String,
        val name: String,
        val eventTitle: String?,
        val startsAt: Instant?,
        val endsAt: Instant?,
        val location: String?,
        val newCapacity: Int,
        val totalCount: Int,
        val registrationStartsAt: Instant?,
        val registrationEndsAt: Instant,
        val description: String?,
        val publicId: String,
        val registrationPublicId: String,
        val waitingNum: Int?,
    )

    data class WaitlistPromotionEmailData(
        val toEmail: String,
        val eventTitle: String?,
        val name: String,
        val waitingNum: Int?,
        val startsAt: Instant?,
        val endsAt: Instant?,
        val location: String?,
        val totalCount: Int?,
        val capacity: Int?,
        val registrationStartsAt: Instant?,
        val registrationEndsAt: Instant?,
        val description: String?,
        val eventPublicId: String,
        val registrationPublicId: String,
    )

    fun sendRegistrationStatusEmail(data: RegistrationStatusEmailData) {
        when (data.status) {
            RegistrationStatus.CONFIRMED -> {
                val htmlContent =
                    loadTemplate("registration-confirmed.html")
                        .replace("{serviceDomain}", emailConfig.serviceDomain)
                        .replace("{name}", data.name)
                        .replace("{eventTitle}", formatEventTitle(data.eventTitle))
                        .replace("{eventDateRange}", formatEventDateRange(data.startsAt, data.endsAt, "-"))
                        .replace("{location}", formatLocation(data.location))
                        .replace("{capacityDisplay}", formatCapacity(data.totalCount, data.capacity))
                        .replace(
                            "{registrationDateRange}",
                            formatRegistrationDateRange(data.registrationStartsAt, data.registrationEndsAt),
                        ).replace("{description}", formatDescription(data.description))
                        .replace("{publicId}", data.publicId ?: "-")
                        .replace("{registrationPublicId}", data.registrationPublicId ?: "-")

                sendHtmlEmail(
                    to = data.toEmail,
                    subject = emailName("참여 확정", data.eventTitle),
                    htmlContent = htmlContent,
                )

                logger.info("신청 확정 정보가 ${data.toEmail} 로 전달되었습니다.")
            }

            RegistrationStatus.WAITLISTED -> {
                val htmlContent =
                    loadTemplate("registration-waitlisted.html")
                        .replace("{serviceDomain}", emailConfig.serviceDomain)
                        .replace("{name}", data.name)
                        .replace("{waitingNum}", data.waitingNum?.toString() ?: "-")
                        .replace("{eventTitle}", formatEventTitle(data.eventTitle))
                        .replace("{eventDateRange}", formatEventDateRange(data.startsAt, data.endsAt, "-"))
                        .replace("{location}", formatLocation(data.location))
                        .replace("{capacityDisplay}", formatCapacity(data.totalCount, data.capacity))
                        .replace(
                            "{registrationDateRange}",
                            formatRegistrationDateRange(data.registrationStartsAt, data.registrationEndsAt),
                        ).replace("{description}", formatDescription(data.description))
                        .replace("{publicId}", data.publicId ?: "-")
                        .replace("{registrationPublicId}", data.registrationPublicId ?: "-")

                sendHtmlEmail(
                    to = data.toEmail,
                    subject = emailName("대기 등록", data.eventTitle),
                    htmlContent = htmlContent,
                )

                logger.info("신청 대기 정보가 ${data.toEmail} 로 전달되었습니다.")
            }
            RegistrationStatus.BANNED -> {
                val htmlContent =
                    loadTemplate("registration-banned.html")
                        .replace("{name}", data.name)
                        .replace("{eventTitle}", formatEventTitle(data.eventTitle))
                        .replace("{eventDateRange}", formatEventDateRange(data.startsAt, data.endsAt, "-"))
                        .replace("{location}", formatLocation(data.location))
                        .replace("{capacityDisplay}", formatCapacity(data.totalCount, data.capacity))
                        .replace(
                            "{registrationDateRange}",
                            formatRegistrationDateRange(data.registrationStartsAt, data.registrationEndsAt),
                        ).replace("{description}", formatDescription(data.description))

                sendHtmlEmail(
                    to = data.toEmail,
                    subject = emailName("강제 취소", data.eventTitle),
                    htmlContent = htmlContent,
                )

                logger.info("신청 강제 취소 정보가 ${data.toEmail} 로 전달되었습니다.")
            }
            else -> {
                logger.info("신청 상태가 ${data.status} 이라 메일을 전송하지 않았습니다: ${data.toEmail}")
            }
        }
    }

    fun sendRegistrationDeleteEmail(data: RegistrationDeleteEmailData) {
        val htmlContent =
            loadTemplate("registration-delete.html")
                .replace("{serviceDomain}", emailConfig.serviceDomain)
                .replace("{name}", data.name)
                .replace("{eventTitle}", formatEventTitle(data.eventTitle))
                .replace("{eventDateRange}", formatEventDateRange(data.startsAt, data.endsAt, "-"))
                .replace("{location}", formatLocation(data.location))
                .replace("{capacityDisplay}", formatCapacity(data.totalCount, data.capacity))
                .replace(
                    "{registrationDateRange}",
                    formatRegistrationDateRange(data.registrationStartsAt, data.registrationEndsAt),
                ).replace("{description}", formatDescription(data.description))
                .replace("{publicId}", data.publicId ?: "-")

        sendHtmlEmail(
            to = data.toEmail,
            subject = emailName("참여 신청 취소", data.eventTitle),
            htmlContent = htmlContent,
        )

        logger.info("신청 삭제 정보가 ${data.toEmail} 로 전달되었습니다.")
    }

    fun sendEventCancellationEmail(data: EventCancellationEmailData) {
        val htmlContent =
            loadTemplate("event-cancelled.html")
                .replace("{name}", data.name)
                .replace("{eventTitle}", formatEventTitle(data.eventTitle))
                .replace("{eventDateRange}", formatEventDateRange(data.startsAt, data.endsAt, "-"))
                .replace("{location}", formatLocation(data.location))
                .replace("{hostEmail}", data.hostEmail)
                .replace("{description}", formatDescription(data.description))

        sendHtmlEmail(
            to = data.toEmail,
            subject = emailName("일정 취소", data.eventTitle),
            htmlContent = htmlContent,
        )

        logger.info("일정 취소 정보가 ${data.toEmail} 로 전달되었습니다.")
    }

    fun sendDemotionEmail(data: DemotionEmailData) {
        val htmlContent =
            loadTemplate("registration-demoted.html")
                .replace("{serviceDomain}", emailConfig.serviceDomain)
                .replace("{name}", data.name)
                .replace("{waitingNum}", data.waitingNum?.toString() ?: "-")
                .replace("{capacityDisplay}", formatCapacity(data.totalCount, data.newCapacity))
                .replace("{eventTitle}", formatEventTitle(data.eventTitle))
                .replace("{eventDateRange}", formatEventDateRange(data.startsAt, data.endsAt, "-"))
                .replace("{location}", formatLocation(data.location))
                .replace(
                    "{registrationDateRange}",
                    formatRegistrationDateRange(data.registrationStartsAt, data.registrationEndsAt),
                ).replace("{description}", formatDescription(data.description))
                .replace("{publicId}", data.publicId)
                .replace("{registrationPublicId}", data.registrationPublicId)

        sendHtmlEmail(
            to = data.toEmail,
            subject = emailName("대기자로 전환", data.eventTitle),
            htmlContent = htmlContent,
        )

        logger.info("정원 축소 대기 변경 알림이 ${data.toEmail} 로 전달되었습니다.")
    }

    fun sendWaitlistPromotionEmail(data: WaitlistPromotionEmailData) {
        sendWaitlistPromotionEmail(
            toEmail = data.toEmail,
            eventTitle = data.eventTitle,
            name = data.name,
            waitingNum = data.waitingNum,
            startsAt = data.startsAt,
            endsAt = data.endsAt,
            location = data.location,
            totalCount = data.totalCount,
            capacity = data.capacity,
            registrationStartsAt = data.registrationStartsAt,
            registrationEndsAt = data.registrationEndsAt,
            description = data.description,
            eventPublicId = data.eventPublicId,
            registrationPublicId = data.registrationPublicId,
        )
    }

    fun sendWaitlistPromotionEmail(
        toEmail: String,
        eventTitle: String?,
        name: String,
        waitingNum: Int?,
        startsAt: Instant?,
        endsAt: Instant?,
        location: String?,
        totalCount: Int?,
        capacity: Int?,
        registrationStartsAt: Instant?,
        registrationEndsAt: Instant?,
        description: String?,
        eventPublicId: String,
        registrationPublicId: String,
    ) {
        val htmlContent =
            loadTemplate("registration-waitlist-promoted.html")
                .replace("{serviceDomain}", emailConfig.serviceDomain)
                .replace("{name}", name)
                .replace("{waitingNum}", waitingNum?.toString() ?: "-")
                .replace("{eventTitle}", formatEventTitle(eventTitle))
                .replace("{eventDateRange}", formatEventDateRange(startsAt, endsAt, "~"))
                .replace("{location}", formatLocation(location))
                .replace("{capacityDisplay}", formatCapacity(totalCount, capacity))
                .replace(
                    "{registrationDateRange}",
                    formatRegistrationDateRange(registrationStartsAt, registrationEndsAt),
                ).replace("{description}", formatDescription(description))
                .replace("{publicId}", eventPublicId)
                .replace("{registrationPublicId}", registrationPublicId)

        sendHtmlEmail(
            to = toEmail,
            subject = emailName("참여 확정", eventTitle),
            htmlContent = htmlContent,
        )

        logger.info("신청 대기 후 확정 정보가 $toEmail 로 전달되었습니다.")
    }

    private fun formatInstant(instant: Instant?): String =
        instant?.atZone(ZoneId.of("Asia/Seoul"))?.format(KOREAN_DATETIME_FORMATTER) ?: "-"

    private fun formatEventTitle(title: String?): String = title ?: ""

    private fun formatLocation(location: String?): String =
        if (location.isNullOrBlank()) {
            "미정"
        } else {
            location
        }

    private fun emailName(
        title: String,
        eventTitle: String?,
    ): String {
        val safeEventTitle = if (eventTitle.isNullOrBlank()) "제목 미정 모임" else eventTitle
        return "[모이밍] $title: $safeEventTitle"
    }

    private fun formatEventDateRange(
        start: Instant?,
        end: Instant?,
        separator: String,
    ): String {
        val startText = start?.let { formatInstant(it) } ?: "미정"
        val endText = end?.let { formatInstant(it) }
        return if (endText.isNullOrBlank()) {
            startText
        } else {
            "$startText$separator$endText"
        }
    }

    private fun formatRegistrationDateRange(
        start: Instant?,
        end: Instant?,
    ): String {
        val startText = start?.let { formatInstant(it) }
        val endText = end?.let { formatInstant(it) }
        return when {
            startText.isNullOrBlank() && endText.isNullOrBlank() -> ""
            startText.isNullOrBlank() -> endText ?: ""
            endText.isNullOrBlank() -> startText
            else -> "$startText-$endText"
        }
    }

    private fun formatCapacity(
        totalCount: Int?,
        capacity: Int?,
    ): String {
        if (capacity == null) return "${totalCount ?: "-"}명"
        val confirmed = if (totalCount != null) minOf(totalCount, capacity) else capacity
        val waitlisted = if (totalCount != null) maxOf(0, totalCount - capacity) else 0
        return if (waitlisted > 0) {
            "$confirmed/${capacity}명 (대기자 ${waitlisted}명)"
        } else {
            "$confirmed/${capacity}명"
        }
    }

    private fun formatDescription(description: String?): String {
        if (description == null) return "-"
        return description.replace("\r\n", "\n").replace("\n", "<br>")
    }

    companion object {
        private val KOREAN_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
    }
}
