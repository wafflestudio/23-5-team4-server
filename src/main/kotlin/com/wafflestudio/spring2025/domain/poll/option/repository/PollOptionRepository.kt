package com.wafflestudio.spring2025.domain.poll.option.repository

import com.wafflestudio.spring2025.domain.poll.option.model.PollOption
import org.springframework.data.repository.ListCrudRepository

interface PollOptionRepository : ListCrudRepository<PollOption, Long> {
    fun findByPollId(pollId: Long): List<PollOption>
}