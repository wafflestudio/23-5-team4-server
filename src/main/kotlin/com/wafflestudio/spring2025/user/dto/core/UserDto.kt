package com.wafflestudio.spring2025.user.dto.core

import com.wafflestudio.spring2025.user.model.User

data class UserDto(
    val id: Long,
    val username: String,
) {
    constructor(user: User) : this(user.id!!, user.username)
}
