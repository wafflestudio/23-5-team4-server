package com.wafflestudio.spring2025.group.repository

import com.wafflestudio.spring2025.group.model.Group
import org.springframework.data.repository.ListCrudRepository

interface GroupRepository : ListCrudRepository<Group, Long> {
    fun findByCode(code: String): Group?
    fun existsByCode(code: String): Boolean
    fun existsByName(name: String): Boolean
}
