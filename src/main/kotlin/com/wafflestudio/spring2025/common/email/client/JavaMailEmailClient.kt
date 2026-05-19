package com.wafflestudio.spring2025.common.email.client

import com.wafflestudio.spring2025.common.email.exception.EmailErrorCode
import com.wafflestudio.spring2025.common.email.exception.EmailServiceUnavailableException
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

@Primary
@Component
class JavaMailEmailClient(
    private val javaMailSender: JavaMailSender,
) : EmailClient {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun sendEmail(
        to: String,
        subject: String,
        htmlContent: String,
        fromEmail: String,
        fromName: String,
    ) {
        // OCI Email Delivery의 SMTP 엔드포인트를 사용 (SDK가 아닌 SMTP 방식)
        // 발신 주소는 OCI 콘솔 Approved Senders에 등록되어 있어야 합니다.
        try {
            val message: MimeMessage = javaMailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, "UTF-8")

            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(htmlContent, true)
            helper.setFrom("$fromName <$fromEmail>")

            javaMailSender.send(message)
        } catch (e: MessagingException) {
            logger.error("메일 구성 실패: to={}, subject={}", to, subject, e)
            throw EmailServiceUnavailableException(EmailErrorCode.EMAIL_SERVICE_UNAVAILABLE)
        } catch (e: MailException) {
            logger.error("메일 전송 실패: to={}, subject={}", to, subject, e)
            throw EmailServiceUnavailableException(EmailErrorCode.EMAIL_SERVICE_UNAVAILABLE)
        }
    }
}
