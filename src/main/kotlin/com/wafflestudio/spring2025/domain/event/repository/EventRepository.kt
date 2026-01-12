package com.wafflestudio.spring2025.domain.event.repository

import com.wafflestudio.spring2025.domain.event.model.Event
import org.springframework.data.repository.ListCrudRepository
import java.time.Instant

interface EventRepository : ListCrudRepository<Event, Long> {
    fun findByGroupIdOrderByStartTimeDesc(groupId: Long): List<Event>
    fun findByGroupIdAndStartTimeAfterOrderByStartTimeAsc(groupId: Long, now: Instant): List<Event>
    fun findTop3ByGroupIdAndStartTimeAfterOrderByStartTimeAsc(groupId: Long, now: Instant): List<Event>
}