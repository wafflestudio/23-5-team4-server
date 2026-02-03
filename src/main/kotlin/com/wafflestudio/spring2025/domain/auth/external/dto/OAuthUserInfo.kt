package com.wafflestudio.spring2025.domain.auth.external.dto

import com.wafflestudio.spring2025.domain.auth.model.SocialProvider

data class OAuthUserInfo(
    val provider: SocialProvider,
    val providerUserId: String,
    val name: String,
    val email: String,
    val profilePicture: String,
)
