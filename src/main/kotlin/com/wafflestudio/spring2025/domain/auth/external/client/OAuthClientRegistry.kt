package com.wafflestudio.spring2025.domain.auth.external.client

import com.wafflestudio.spring2025.domain.auth.model.SocialProvider
import org.springframework.stereotype.Component

@Component
class OAuthClientRegistry(
    clients: List<OAuthClient>,
) {
    private val map: Map<SocialProvider, OAuthClient> = clients.associateBy { it.provider }

    fun getClient(provider: SocialProvider): OAuthClient =
        map[provider] ?: throw IllegalArgumentException("Unsupported provider: $provider")
}
