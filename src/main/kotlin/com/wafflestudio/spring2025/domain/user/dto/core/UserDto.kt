package com.wafflestudio.spring2025.domain.user.dto.core

import com.wafflestudio.spring2025.domain.user.model.User

data class UserDto(
    val id: Long,
    val email: String,
    val name: String,
    val profileImage: String?,
) {
    constructor(user: User) : this(user.id!!, user.email, user.name, user.profileImage)
}
