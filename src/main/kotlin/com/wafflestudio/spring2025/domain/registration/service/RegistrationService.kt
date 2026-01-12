package com.wafflestudio.spring2025.domain.registration.service

import com.wafflestudio.spring2025.domain.registration.dto.core.RegistrationDto
import com.wafflestudio.spring2025.domain.registration.repository.RegistrationRepository
import org.springframework.stereotype.Service

@Service
class RegistrationService(
    private val registrationRepository: RegistrationRepository,
) {
    fun create(userId: Long, groupId: Long): RegistrationDto {
        TODO("동아리 가입 신청 생성 구현")
    }

    fun getByGroupId(groupId: Long): List<RegistrationDto> {
        TODO("동아리별 가입 신청 목록 조회 구현")
    }

    fun approve(registrationId: Long) {
        TODO("가입 신청 승인 (Member 생성 후 Registration 삭제) 구현")
    }

    fun reject(registrationId: Long) {
        TODO("가입 신청 거절 (Registration 삭제) 구현")
    }
}