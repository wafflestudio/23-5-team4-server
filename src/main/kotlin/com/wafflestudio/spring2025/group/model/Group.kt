package com.wafflestudio.spring2025.group.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("groups")
class Group(
    @Id var id: Long? = null,
    var name: String,
    var code: String,
    var detail: String,
)
