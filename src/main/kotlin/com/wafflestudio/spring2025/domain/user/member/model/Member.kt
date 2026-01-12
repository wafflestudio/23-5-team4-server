package com.wafflestudio.spring2025.domain.user.member.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("members")
class Member(
    @Id var id: Long? = null,
    var userId: Long,
    var groupId: Long,
    var nickname: String,
    var type: MemberType,
)