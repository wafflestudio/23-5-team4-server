package com.wafflestudio.spring2025.domain.poll.option.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("poll_options")
class PollOption(
    @Id var id: Long? = null,
    var pollId: Long,
    var content: String,
)