package com.wafflestudio.spring2025.domain.auth.controller

import com.wafflestudio.spring2025.domain.auth.dto.LoginResponse
import com.wafflestudio.spring2025.domain.auth.dto.SocialLoginRequest
import com.wafflestudio.spring2025.domain.auth.model.SocialProvider
import com.wafflestudio.spring2025.domain.auth.service.SocialAuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/auth/social")
class SocialAuthController(
    private val socialAuthService: SocialAuthService,
) {
    @PostMapping
    suspend fun social(
        @RequestBody socialLoginRequest: SocialLoginRequest,
    ): ResponseEntity<LoginResponse> {
        val response =
            socialAuthService.socialLogin(
                provider = SocialProvider.valueOf(socialLoginRequest.provider),
                code = socialLoginRequest.code,
            )
        return ResponseEntity.ok(response)
    }
}
