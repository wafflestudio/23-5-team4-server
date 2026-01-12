package com.wafflestudio.spring2025.domain.poll.vote.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("votes")
class Vote(
    @Id var id: Long? = null,
    var pollOptionId: Long,
    var memberId: Long,
)