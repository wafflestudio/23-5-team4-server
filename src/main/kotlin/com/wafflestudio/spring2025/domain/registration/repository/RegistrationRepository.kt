package com.wafflestudio.spring2025.domain.registration.repository

import com.wafflestudio.spring2025.domain.registration.model.Registration
import org.springframework.data.repository.ListCrudRepository

interface RegistrationRepository : ListCrudRepository<Registration, Long> {
    fun findByGroupId(groupId: Long): List<Registration>
    fun findByUserId(userId: Long): List<Registration>
    fun findByUserIdAndGroupId(userId: Long, groupId: Long): Registration?
    fun existsByUserIdAndGroupId(userId: Long, groupId: Long): Boolean
}