package com.wafflestudio.spring2025.socialaccount.dto.core

import com.wafflestudio.spring2025.socialaccount.model.SocialAccount
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "소셜 계정 정보")
data class SocialAccountDto(
    @Schema(description = "소셜 계정 ID")
    val id: Long,

    @Schema(description = "사용자 ID")
    val userId: Long,

    @Schema(description = "소셜 로그인 제공자")
    val provider: String,

    @Schema(description = "제공자 사용자 ID")
    val providerUserId: String,

    @Schema(description = "생성 일시 (epoch milliseconds)")
    val createdAt: Long,
) {
    constructor(socialAccount: SocialAccount) : this(
        id = socialAccount.id!!,
        userId = socialAccount.userId,
        provider = socialAccount.provider,
        providerUserId = socialAccount.providerUserId,
        createdAt = socialAccount.createdAt!!.toEpochMilli(),
    )
}
