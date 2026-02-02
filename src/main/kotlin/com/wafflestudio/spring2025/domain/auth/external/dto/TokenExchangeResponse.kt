package com.wafflestudio.spring2025.domain.auth.external.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenExchangeResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("expires_in")
    val expiresIn: Int,
    @JsonProperty("refresh_token")
    val refreshToken: String? = null,
    @JsonProperty("refresh_token_expires_in")
    val refreshTokenExpiresIn: Int? = null,
    @JsonProperty("scope")
    val scope: String? = null,
    @JsonProperty("token_type")
    val tokenType: String,
)
