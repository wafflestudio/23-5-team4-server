package com.wafflestudio.spring2025.domain.poll.repository

import com.wafflestudio.spring2025.domain.poll.model.Poll
import org.springframework.data.repository.ListCrudRepository
import java.time.Instant

interface PollRepository : ListCrudRepository<Poll, Long> {
    fun findByGroupIdOrderByCreatedAtDesc(groupId: Long): List<Poll>
    fun findByGroupIdAndDeadlineAfterOrderByCreatedAtDesc(groupId: Long, now: Instant): List<Poll>
    fun findTop3ByGroupIdAndDeadlineAfterOrderByCreatedAtDesc(groupId: Long, now: Instant): List<Poll>
}