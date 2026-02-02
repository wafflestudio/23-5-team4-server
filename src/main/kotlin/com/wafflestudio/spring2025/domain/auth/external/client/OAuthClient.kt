package com.wafflestudio.spring2025.domain.auth.external.client

interface OAuthClient {
    suspend fun exchangeToken(code: String): String
}
