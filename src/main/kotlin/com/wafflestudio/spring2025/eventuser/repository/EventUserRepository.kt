package com.wafflestudio.spring2025.eventuser.repository

import com.wafflestudio.spring2025.eventuser.model.EventUser
import com.wafflestudio.spring2025.eventuser.model.EventUserStatus
import org.springframework.data.repository.ListCrudRepository

interface EventUserRepository : ListCrudRepository<EventUser, Long> {
    fun findByEventId(eventId: Long): List<EventUser>
    fun findByUserId(userId: Long): List<EventUser>
    fun findByEventIdAndUserId(eventId: Long, userId: Long): EventUser?
    fun existsByEventIdAndUserId(eventId: Long, userId: Long): Boolean
    fun countByEventIdAndStatus(eventId: Long, status: EventUserStatus): Long
    fun findByEventIdAndStatusOrderByWaitingNumberAsc(eventId: Long, status: EventUserStatus): List<EventUser>
}
