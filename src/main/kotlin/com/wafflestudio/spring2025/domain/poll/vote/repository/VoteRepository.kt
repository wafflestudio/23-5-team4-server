package com.wafflestudio.spring2025.domain.poll.vote.repository

import com.wafflestudio.spring2025.domain.poll.vote.model.Vote
import org.springframework.data.repository.ListCrudRepository

interface VoteRepository : ListCrudRepository<Vote, Long> {
    fun findByPollOptionId(pollOptionId: Long): List<Vote>
    fun findByMemberId(memberId: Long): List<Vote>
    fun countByPollOptionId(pollOptionId: Long): Long
}