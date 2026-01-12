package com.wafflestudio.spring2025.domain.auth.service

import com.wafflestudio.spring2025.domain.auth.JwtTokenProvider
import com.wafflestudio.spring2025.domain.auth.exception.AuthenticateException
import com.wafflestudio.spring2025.domain.auth.exception.SignUpBadEmailException
import com.wafflestudio.spring2025.domain.auth.exception.SignUpEmailConflictException
import com.wafflestudio.spring2025.domain.user.dto.core.UserDto
import com.wafflestudio.spring2025.domain.user.model.User
import com.wafflestudio.spring2025.domain.user.repository.UserRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val redisTemplate: StringRedisTemplate,
    private val jwtBlacklistService: JwtBlacklistService,
) {
    fun register(
        email: String,
        name: String,
        profileImage: String?,
    ): UserDto {
        if (email.isBlank()) {
            throw SignUpBadEmailException()
        }

        if (userRepository.existsByEmail(email)) {
            throw SignUpEmailConflictException()
        }

        val user =
            userRepository.save(
                User(
                    email = email,
                    name = name,
                    profileImage = profileImage,
                ),
            )
        return UserDto(user)
    }

    fun login(
        email: String,
    ): String {
        val user = userRepository.findByEmail(email) ?: throw AuthenticateException()
        val accessToken = jwtTokenProvider.createToken(user.email)
        return accessToken
    }

    fun logout(
        user: User,
        token: String,
    ) {
        jwtBlacklistService.addToBlacklist(token)
    }
}
