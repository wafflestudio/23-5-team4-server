package com.wafflestudio.spring2025.common.email.outbox.repository

import com.wafflestudio.spring2025.common.email.outbox.model.EmailOutbox
import org.springframework.data.repository.ListCrudRepository

interface EmailOutboxRepository : ListCrudRepository<EmailOutbox, Long>
