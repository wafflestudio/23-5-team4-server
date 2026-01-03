package com.wafflestudio.spring2025.user.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원가입 요청")
data class RegisterRequest(
    @Schema(description = "사용자 아이디 (4자 이상)", example = "user1234", required = true)
    val username: String,
    @Schema(description = "비밀번호 (4자 이상)", example = "password1234", required = true)
    val password: String,
)
