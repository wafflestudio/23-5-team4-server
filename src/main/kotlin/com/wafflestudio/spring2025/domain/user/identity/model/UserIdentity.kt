package com.wafflestudio.spring2025.domain.user.identity.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_identities")
class UserIdentity(
    @Id var id: Long? = null,
    @Column("user_id")
    var userId: Long,
    var provider: String,
    @Column("provider_user_id")
    var providerUserId: String,
    @CreatedDate
    var createdAt: Instant? = null,
)
