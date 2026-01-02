package com.wafflestudio.spring2025.member.repository

import com.wafflestudio.spring2025.member.model.Member
import com.wafflestudio.spring2025.member.model.MemberType
import org.springframework.data.repository.ListCrudRepository

interface MemberRepository : ListCrudRepository<Member, Long> {
    fun findByGroupId(groupId: Long): List<Member>
    fun findByUserId(userId: Long): List<Member>
    fun findByUserIdAndGroupId(userId: Long, groupId: Long): Member?
    fun existsByUserIdAndGroupId(userId: Long, groupId: Long): Boolean
    fun findByGroupIdOrderByTypeAsc(groupId: Long): List<Member>
    fun countByGroupIdAndType(groupId: Long, type: MemberType): Long
}
