package com.wafflestudio.spring2025.domain.auth.dto

data class LoginRequest(
    val email: String,
    val password: String,
)
