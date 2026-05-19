package com.wafflestudio.spring2025.domain.auth.dto

data class SignupRequest(
    val email: String,
    val name: String,
    val password: String,
    val profileImage: String? = null,
)
