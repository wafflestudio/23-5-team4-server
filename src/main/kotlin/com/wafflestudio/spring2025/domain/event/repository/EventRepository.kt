package com.wafflestudio.spring2025.domain.event.repository

import com.wafflestudio.spring2025.domain.event.model.Event
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.ListCrudRepository
import java.time.Instant

interface EventRepository : ListCrudRepository<Event, Long> {
    fun findByPublicId(publicId: String): Event?

    fun findByCreatedByAndCreatedAtIsNotNullOrderByCreatedAtDesc(
        createdBy: Long,
        pageable: Pageable,
    ): List<Event>

    fun findByCreatedByAndCreatedAtIsNotNullAndCreatedAtLessThanOrderByCreatedAtDesc(
        createdBy: Long,
        cursor: Instant,
        pageable: Pageable,
    ): List<Event>
}
