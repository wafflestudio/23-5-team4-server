package com.wafflestudio.spring2025.user.repository

import com.wafflestudio.spring2025.user.model.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {
    fun findByUsername(username: String): User?

    fun existsByUsername(username: String): Boolean
}
