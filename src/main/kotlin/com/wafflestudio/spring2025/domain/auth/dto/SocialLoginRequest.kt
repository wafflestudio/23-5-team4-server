package com.wafflestudio.spring2025.domain.auth.dto

data class SocialLoginRequest(
    val provider: String,
    val code: String,
)
