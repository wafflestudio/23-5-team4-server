package com.wafflestudio.spring2025.polloption.repository

import com.wafflestudio.spring2025.polloption.model.PollOption
import org.springframework.data.repository.ListCrudRepository

interface PollOptionRepository : ListCrudRepository<PollOption, Long> {
    fun findByPollId(pollId: Long): List<PollOption>
}
