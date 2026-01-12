package com.wafflestudio.spring2025.domain.user.socialaccount.repository

import com.wafflestudio.spring2025.domain.user.socialaccount.model.SocialAccount
import org.springframework.data.repository.ListCrudRepository

interface SocialAccountRepository : ListCrudRepository<SocialAccount, Long> {
    fun findByProviderAndProviderUserId(provider: String, providerUserId: String): SocialAccount?
    fun findByUserId(userId: Long): List<SocialAccount>
}