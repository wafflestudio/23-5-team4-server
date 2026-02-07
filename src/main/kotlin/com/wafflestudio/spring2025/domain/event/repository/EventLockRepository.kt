package com.wafflestudio.spring2025.domain.event.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class EventLockRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun lockById(eventId: Long) {
        jdbcTemplate.queryForObject(
            "SELECT id FROM events WHERE id = ? FOR UPDATE",
            Long::class.java,
            eventId,
        )
    }
}
