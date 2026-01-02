package com.wafflestudio.spring2025.user.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "로그인 요청")
data class LoginRequest(
    @Schema(description = "사용자 아이디", example = "user1234", required = true)
    val username: String,
    @Schema(description = "비밀번호", example = "password1234", required = true)
    val password: String,
)
