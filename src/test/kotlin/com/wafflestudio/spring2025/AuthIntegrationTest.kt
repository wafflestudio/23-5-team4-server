package com.wafflestudio.spring2025

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.spring2025.domain.auth.dto.LoginRequest
import com.wafflestudio.spring2025.domain.auth.dto.SignupRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
class AuthIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val mapper: ObjectMapper,
        private val dataGenerator: DataGenerator,
    ) {
        // ==================== Signup ====================

        @Test
        fun `should send verification email on signup`() {
            val request = SignupRequest("newuser@example.com", "newuser", "password123")
            mvc
                .perform(
                    post("/api/auth/signup")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isNoContent)
        }

        @Test
        fun `should reject signup with invalid email`() {
            val request = SignupRequest("", "user", "password123")
            mvc
                .perform(
                    post("/api/auth/signup")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value("BAD_EMAIL"))
        }

        @Test
        fun `should reject signup with short password`() {
            val request = SignupRequest("short@example.com", "user", "ab1")
            mvc
                .perform(
                    post("/api/auth/signup")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value("BAD_PASSWORD"))
        }

        @Test
        fun `should reject signup with password missing digits`() {
            val request = SignupRequest("nodigit@example.com", "user", "abcdefgh")
            mvc
                .perform(
                    post("/api/auth/signup")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value("BAD_PASSWORD"))
        }

        @Test
        fun `should reject signup with password missing letters`() {
            val request = SignupRequest("noletter@example.com", "user", "12345678")
            mvc
                .perform(
                    post("/api/auth/signup")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value("BAD_PASSWORD"))
        }

        @Test
        fun `should reject signup with blank name`() {
            val request = SignupRequest("blankname@example.com", " ", "password123")
            mvc
                .perform(
                    post("/api/auth/signup")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value("BAD_NAME"))
        }

        @Test
        fun `should reject signup when email already registered`() {
            val (user, _) = dataGenerator.generateUserWithPassword()
            val request = SignupRequest(user.email, "user", "password123")
            mvc
                .perform(
                    post("/api/auth/signup")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isConflict)
                .andExpect(jsonPath("$.code").value("EMAIL_ACCOUNT_ALREADY_EXIST"))
        }

        @Test
        fun `should reject signup when pending user already exists`() {
            val pendingUser = dataGenerator.generatePendingUser()
            val request = SignupRequest(pendingUser.email, "user", "password123")
            mvc
                .perform(
                    post("/api/auth/signup")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isConflict)
                .andExpect(jsonPath("$.code").value("EMAIL_ACCOUNT_ALREADY_EXIST"))
        }

        // ==================== Email Verification ====================

        @Test
        fun `should verify email and create user`() {
            val pendingUser = dataGenerator.generatePendingUser()
            mvc
                .perform(
                    post("/api/auth/email-verification/${pendingUser.verificationCode}")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isOk)
        }

        @Test
        fun `should reject invalid verification code`() {
            mvc
                .perform(
                    post("/api/auth/email-verification/invalid-code")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value("INVALID_VERIFICATION_CODE"))
        }

        @Test
        fun `should reject expired verification code`() {
            val pendingUser = dataGenerator.generatePendingUser(expiresAt = Instant.now().minusSeconds(1))
            mvc
                .perform(
                    post("/api/auth/email-verification/${pendingUser.verificationCode}")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value("INVALID_VERIFICATION_CODE"))
        }

        // ==================== Login ====================

        @Test
        fun `should login with email successfully`() {
            val password = "myPassword1"
            val (user, _) = dataGenerator.generateUserWithPassword(password)
            val request = LoginRequest(email = user.email, password = password)
            mvc
                .perform(
                    post("/api/auth/login")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.token").isNotEmpty)
        }

        @Test
        fun `should reject login with wrong email`() {
            val request = LoginRequest(email = "wrong@example.com", password = "something1")
            mvc
                .perform(
                    post("/api/auth/login")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isUnauthorized)
                .andExpect(jsonPath("$.code").value("LOGIN_FAILED"))
        }

        @Test
        fun `should reject login with wrong password`() {
            val (user, _) = dataGenerator.generateUserWithPassword("correctPass1")
            val request = LoginRequest(email = user.email, password = "wrongPass1")
            mvc
                .perform(
                    post("/api/auth/login")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isUnauthorized)
                .andExpect(jsonPath("$.code").value("LOGIN_FAILED"))
        }

        // ==================== User Info ====================

        @Test
        fun `should retrieve own user information`() {
            val (user, token) = dataGenerator.generateUser()
            mvc
                .perform(
                    get("/api/users/me")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.email").value(user.email))
                .andExpect(jsonPath("$.name").value(user.name))
        }

        @Test
        fun `should reject user info request with invalid token`() {
            mvc
                .perform(
                    get("/api/users/me")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isUnauthorized)
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"))
        }

        @Test
        fun `should reject user info request without token`() {
            mvc
                .perform(
                    get("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isUnauthorized)
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"))
        }

        // ==================== Logout & JWT Blacklist ====================

        @Test
        fun `should blacklist JWT on logout`() {
            val (user, token) = dataGenerator.generateUser()

            // /me 정상 접근
            mvc
                .perform(
                    get("/api/users/me")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.email").value(user.email))

            // 로그아웃
            mvc
                .perform(
                    post("/api/auth/logout")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isNoContent)

            // 블랙리스트된 토큰으로 /me 접근 시 401
            mvc
                .perform(
                    get("/api/users/me")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isUnauthorized)
        }

        // ==================== Full Flow ====================

        @Test
        fun `should complete full signup-verify-login flow`() {
            // 1. 회원가입 요청 (PendingUser 생성)
            val pendingUser =
                dataGenerator.generatePendingUser(
                    email = "fullflow@example.com",
                    password = "password123",
                )

            // 2. 이메일 인증
            mvc
                .perform(
                    post("/api/auth/email-verification/${pendingUser.verificationCode}")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isOk)

            // 3. 인증된 이메일로 로그인
            val loginRequest = LoginRequest(email = "fullflow@example.com", password = "password123")
            val loginResult =
                mvc
                    .perform(
                        post("/api/auth/login")
                            .content(mapper.writeValueAsString(loginRequest))
                            .contentType(MediaType.APPLICATION_JSON),
                    ).andExpect(status().isOk)
                    .andExpect(jsonPath("$.token").isNotEmpty)
                    .andReturn()

            // 4. 발급된 토큰으로 내 정보 조회
            val responseBody = mapper.readTree(loginResult.response.contentAsString)
            val token = responseBody.get("token").asText()
            mvc
                .perform(
                    get("/api/users/me")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.email").value("fullflow@example.com"))
        }
    }
