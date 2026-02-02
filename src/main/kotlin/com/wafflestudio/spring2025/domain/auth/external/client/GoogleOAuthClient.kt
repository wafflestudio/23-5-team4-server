package com.wafflestudio.spring2025.domain.auth.external.client

import com.wafflestudio.spring2025.domain.auth.AuthenticateException
import com.wafflestudio.spring2025.domain.auth.external.dto.TokenExchangeResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

class GoogleOAuthClient(
    private val webClient: WebClient,
    @Value("\${oauth.providers.google.client-id}") private val clientId: String,
    @Value("\${oauth.providers.google.client-secret}") private val clientSecret: String,
    @Value("\${oauth.providers.google.redirect-uri}") private val redirectUri: String
): OAuthClient {

    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private val TOKEN_EXCHANGE_URL: String = "https://oauth2.googleapis.com/token"
        private val USER_INFO_URL: String = "https://www.googleapis.com/auth/userinfo.email"
    }

    suspend override fun exchangeToken(
        code: String
    ): String {
        val body = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("code", code)
            add("client_id", clientId)
            add("client_secret", clientSecret)
            add("redirect_uri", redirectUri)
        }
        try {
            val response = webClient
                .post()
                .uri(TOKEN_EXCHANGE_URL)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(body)
                .retrieve()
                .onStatus({ it.isError }) {
                    throw AuthenticateException()
                }.awaitBody<TokenExchangeResponse>()
            return response.accessToken
        } catch (e: Exception){
            throw AuthenticateException()
        }
    }

    suspend fun getUserInfo(
        token: String,
    ){

    }
}
