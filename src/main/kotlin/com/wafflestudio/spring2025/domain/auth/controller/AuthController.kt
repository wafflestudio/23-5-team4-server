package com.wafflestudio.spring2025.domain.auth.controller

import com.wafflestudio.spring2025.domain.auth.dto.LoginRequest
import com.wafflestudio.spring2025.domain.auth.dto.LoginResponse
import com.wafflestudio.spring2025.domain.auth.dto.SignupRequest
import com.wafflestudio.spring2025.domain.auth.service.AuthService
import com.wafflestudio.spring2025.domain.auth.service.EmailVerificationService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService,
) {
    @PostMapping("/signup")
    fun signup(
        @RequestBody signupRequest: SignupRequest,
    ): ResponseEntity<Void> {
        // profileImage는 회원가입에서 받지 않는 정책이면 null로 고정
        emailVerificationService.createPendingUser(
            email = signupRequest.email,
            name = signupRequest.name,
            password = signupRequest.password,
            profileImage = signupRequest.profileImage,
        )

        return ResponseEntity.noContent().build()
    }

    @PostMapping("/email-verification/{verificationCode}")
    fun verifyEmail(
        @PathVariable verificationCode: String,
    ): ResponseEntity<Void> {
        emailVerificationService.verifyEmailAndCreateUser(verificationCode)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequest,
    ): ResponseEntity<LoginResponse> {
        val token = authService.login(loginRequest.email, loginRequest.password)
        return ResponseEntity.ok(LoginResponse(token))
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<Void> {
        val token = resolveToken(request)
        authService.logout(token)
        return ResponseEntity.noContent().build()
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }
}
