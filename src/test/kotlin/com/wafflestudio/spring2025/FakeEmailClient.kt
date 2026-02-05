package com.wafflestudio.spring2025

import com.wafflestudio.spring2025.common.email.client.EmailClient
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("test")
class FakeEmailClient : EmailClient {
    override fun sendEmail(
        to: String,
        subject: String,
        htmlContent: String,
        fromEmail: String,
        fromName: String,
    ) {
        // no-op: do not send actual emails in test
    }
}
