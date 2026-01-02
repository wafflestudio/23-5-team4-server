package com.wafflestudio.spring2025.user.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class JwtBlacklistService(
    private val redisTemplate: RedisTemplate<String, String>,
    @Value("\${jwt.expiration-in-ms}")
    private val expirationInMs: Long,
) {
    fun addToBlacklist(token: String) {
        val key = BLACKLIST_PREFIX + token
        // TTL을 JWT 만료 시간과 동일하게 설정 (메모리 절약)
        redisTemplate.opsForValue().set(key, "true", expirationInMs, TimeUnit.MILLISECONDS)
    }

    fun isBlacklisted(token: String): Boolean {
        val key = BLACKLIST_PREFIX + token
        return redisTemplate.hasKey(key) == true
    }

    companion object {
        private const val BLACKLIST_PREFIX = "jwt:blacklist:"
    }
}
