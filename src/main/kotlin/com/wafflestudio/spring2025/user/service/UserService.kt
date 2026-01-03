package com.wafflestudio.spring2025.user.service

import com.wafflestudio.spring2025.user.AuthenticateException
import com.wafflestudio.spring2025.user.JwtTokenProvider
import com.wafflestudio.spring2025.user.SignUpBadPasswordException
import com.wafflestudio.spring2025.user.SignUpBadUsernameException
import com.wafflestudio.spring2025.user.SignUpUsernameConflictException
import com.wafflestudio.spring2025.user.dto.core.UserDto
import com.wafflestudio.spring2025.user.model.User
import com.wafflestudio.spring2025.user.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt
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
        username: String,
        password: String,
    ): UserDto {
        if (username.length < 4) {
            throw SignUpBadUsernameException()
        }
        if (password.length < 4) {
            throw SignUpBadPasswordException()
        }

        if (userRepository.existsByUsername(username)) {
            throw SignUpUsernameConflictException()
        }

        val encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val user =
            userRepository.save(
                User(
                    username = username,
                    password = encryptedPassword,
                ),
            )
        return UserDto(user)
    }

    fun login(
        username: String,
        password: String,
    ): String {
        val user = userRepository.findByUsername(username) ?: throw AuthenticateException()
        if (BCrypt.checkpw(password, user.password).not()) {
            throw AuthenticateException()
        }
        val accessToken = jwtTokenProvider.createToken(user.username)
        return accessToken
    }

    fun logout(
        user: User,
        token: String,
    ) {
        jwtBlacklistService.addToBlacklist(token)
    }
}
