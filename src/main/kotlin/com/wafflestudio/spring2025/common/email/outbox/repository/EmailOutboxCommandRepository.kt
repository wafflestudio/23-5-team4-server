package com.wafflestudio.spring2025.common.email.outbox.repository

import com.wafflestudio.spring2025.common.email.outbox.model.EmailOutboxStatus
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.Instant

@Repository
class EmailOutboxCommandRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
) {
    fun findProcessableIds(
        limit: Int,
        now: Instant = Instant.now(),
    ): List<Long> {
        val params =
            MapSqlParameterSource()
                .addValue("limit", limit)
                .addValue("now", Timestamp.from(now))
        return jdbcTemplate.query(
            """
            SELECT id
            FROM email_outbox
            WHERE status IN ('PENDING', 'FAILED')
              AND retry_count < max_retry_count
              AND next_retry_at <= :now
            ORDER BY id ASC
            LIMIT :limit
            """.trimIndent(),
            params,
        ) { rs, _ -> rs.getLong("id") }
    }

    fun claimForProcessing(
        id: Long,
        now: Instant = Instant.now(),
    ): Boolean {
        val params =
            MapSqlParameterSource()
                .addValue("id", id)
                .addValue("processing", EmailOutboxStatus.PROCESSING.name)
                .addValue("now", Timestamp.from(now))
        val updated =
            jdbcTemplate.update(
                """
                UPDATE email_outbox
                SET status = :processing,
                    updated_at = CURRENT_TIMESTAMP(6)
                WHERE id = :id
                  AND status IN ('PENDING', 'FAILED')
                  AND retry_count < max_retry_count
                  AND next_retry_at <= :now
                """.trimIndent(),
                params,
            )
        return updated == 1
    }

    fun markSent(id: Long) {
        val params =
            MapSqlParameterSource()
                .addValue("id", id)
                .addValue("sent", EmailOutboxStatus.SENT.name)
        jdbcTemplate.update(
            """
            UPDATE email_outbox
            SET status = :sent,
                sent_at = CURRENT_TIMESTAMP(6),
                last_error = NULL,
                updated_at = CURRENT_TIMESTAMP(6)
            WHERE id = :id
            """.trimIndent(),
            params,
        )
    }

    fun markFailed(
        id: Long,
        retryCount: Int,
        nextRetryAt: Instant,
        lastError: String,
    ) {
        val params =
            MapSqlParameterSource()
                .addValue("id", id)
                .addValue("failed", EmailOutboxStatus.FAILED.name)
                .addValue("retryCount", retryCount)
                .addValue("nextRetryAt", Timestamp.from(nextRetryAt))
                .addValue("lastError", lastError)
        jdbcTemplate.update(
            """
            UPDATE email_outbox
            SET status = :failed,
                retry_count = :retryCount,
                next_retry_at = :nextRetryAt,
                last_error = :lastError,
                updated_at = CURRENT_TIMESTAMP(6)
            WHERE id = :id
            """.trimIndent(),
            params,
        )
    }
}
