package com.wafflestudio.spring2025.domain.user.socialaccount.service

import com.wafflestudio.spring2025.domain.user.socialaccount.dto.core.SocialAccountDto
import com.wafflestudio.spring2025.domain.user.socialaccount.repository.SocialAccountRepository
import org.springframework.stereotype.Service

@Service
class SocialAccountService(
    private val socialAccountRepository: SocialAccountRepository,
) {
    fun linkSocialAccount(userId: Long, provider: String, providerUserId: String): SocialAccountDto {
        TODO("소셜 계정 연동 구현")
    }

    fun getSocialAccountsByUserId(userId: Long): List<SocialAccountDto> {
        TODO("사용자의 소셜 계정 목록 조회 구현")
    }

    fun unlinkSocialAccount(userId: Long, socialAccountId: Long) {
        TODO("소셜 계정 연동 해제 구현")
    }
}