package com.wafflestudio.spring2025.polloption.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("poll_options")
class PollOption(
    @Id var id: Long? = null,
    var pollId: Long,
    var content: String,
)
