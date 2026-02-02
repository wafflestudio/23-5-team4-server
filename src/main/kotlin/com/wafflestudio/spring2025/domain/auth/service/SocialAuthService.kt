package com.wafflestudio.spring2025.domain.auth.service

import com.wafflestudio.spring2025.domain.auth.model.SocialProvider
import com.wafflestudio.spring2025.domain.user.dto.core.UserDto
import com.wafflestudio.spring2025.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class SocialAuthService (
    private val userRepository: UserRepository,
) {
    fun socialRegister(
        // TODO
    ): UserDto {
        TODO("소셜 회원가입 도메인 로직 구현")
    }

    fun socialLogin(
        provider: SocialProvider,
        code: String,
    ){

    }


}
