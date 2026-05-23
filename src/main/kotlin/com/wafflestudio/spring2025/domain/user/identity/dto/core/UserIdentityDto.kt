package com.wafflestudio.spring2025.domain.user.identity.dto.core

import com.wafflestudio.spring2025.domain.user.identity.model.UserIdentity

data class UserIdentityDto(
    val id: Long,
    val userId: Long,
    val provider: String,
    val providerUserId: String,
    val createdAt: Long,
) {
    constructor(identity: UserIdentity) : this(
        id = TODO("UserIdentity -> UserIdentityDto 매핑 구현"),
        userId = TODO("UserIdentity -> UserIdentityDto 매핑 구현"),
        provider = TODO("UserIdentity -> UserIdentityDto 매핑 구현"),
        providerUserId = TODO("UserIdentity -> UserIdentityDto 매핑 구현"),
        createdAt = TODO("UserIdentity -> UserIdentityDto 매핑 구현"),
    )
}
