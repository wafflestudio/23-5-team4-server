package com.wafflestudio.spring2025.domain.auth.external.client

import com.wafflestudio.spring2025.domain.auth.external.dto.OAuthUserInfo
import com.wafflestudio.spring2025.domain.auth.model.SocialProvider

interface OAuthClient {
    val provider: SocialProvider

    suspend fun exchangeToken(code: String): String

    suspend fun getUserInfo(token: String): OAuthUserInfo
}
