package com.wafflestudio.spring2025.domain.group.service

import com.wafflestudio.spring2025.domain.group.dto.core.GroupDto
import com.wafflestudio.spring2025.domain.group.repository.GroupRepository
import org.springframework.stereotype.Service

@Service
class GroupService(
    private val groupRepository: GroupRepository,
) {
    fun create(name: String, detail: String): GroupDto {
        TODO("동아리 생성 (초대 코드 자동 생성) 구현")
    }

    fun getById(groupId: Long): GroupDto {
        TODO("동아리 조회 구현")
    }

    fun getByCode(code: String): GroupDto {
        TODO("초대 코드로 동아리 조회 구현")
    }

    fun list(): List<GroupDto> {
        TODO("동아리 목록 조회 구현")
    }

    fun update(groupId: Long, name: String?, detail: String?): GroupDto {
        TODO("동아리 정보 수정 구현")
    }

    fun delete(groupId: Long) {
        TODO("동아리 삭제 구현")
    }

    fun regenerateCode(groupId: Long): GroupDto {
        TODO("초대 코드 재생성 구현")
    }
}