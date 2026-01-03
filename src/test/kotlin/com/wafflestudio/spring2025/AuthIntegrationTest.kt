package com.wafflestudio.spring2025

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.spring2025.user.dto.RegisterRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
class AuthIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val mapper: ObjectMapper,
    ) {
        @Test
        fun `should register successfully`() {
            // 회원가입할 수 있다
            val username = "username1"
            val password = "qwer1234"

            val request = RegisterRequest(username, password)
            mvc
                .perform(
                    post("/api/v1/auth/register")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isCreated)
        }

        @Test
        fun `should return 400 error when username is less than 4 characters during registration`() {
            // 회원가입 시 유저네임이 4자 미만이면 400 에러
            val username = "use"
            val password = "qwer1234"

            val request = RegisterRequest(username, password)
            mvc
                .perform(
                    post("/api/v1/auth/register")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should return 400 error when password is less than 4 characters during registration`() {
            // 회원가입 시 비밀번호가 4자 미만이면 400 에러
            val username = "username2"
            val password = "qwe"

            val request = RegisterRequest(username, password)
            mvc
                .perform(
                    post("/api/v1/auth/register")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should login successfully`() {
            // 로그인할 수 있다
            val (user, token) = dataGenerator.generateUser(password = "qwer1234")
            val request = RegisterRequest(user.username, "qwer1234")
            mvc
                .perform(
                    post("/api/v1/auth/login")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isCreated)
        }

        @Test
        fun `should return 401 error when username is incorrect during login`() {
            // 로그인 시 유저네임이 틀렸다면 401 에러
            val (user, token) = dataGenerator.generateUser()
            val request = RegisterRequest("wrong-username", "some-password")
            mvc
                .perform(
                    post("/api/v1/auth/login")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isUnauthorized)
        }

        @Test
        fun `should return 401 error when password is incorrect during login`() {
            // 로그인 시 비밀번호가 틀렸다면 401 에러
            val (user, token) = dataGenerator.generateUser()
            val request = RegisterRequest(user.username, "wrong-password")
            mvc
                .perform(
                    post("/api/v1/auth/login")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isUnauthorized)
        }

        @Test
        fun `should retrieve own user information`() {
            // 본인 정보를 조회할 수 있다
            val (user, token) = dataGenerator.generateUser()
            mvc
                .perform(
                    get("/api/v1/users/me")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.username").value(user.username))
        }

        @Test
        fun `should return 401 when retrieving user information with invalid token`() {
            // 유효하지 않은 토큰으로 본인 정보 조회 시 401
            val (user, token) = dataGenerator.generateUser()
            mvc
                .perform(
                    get("/api/v1/users/me")
                        .header("Authorization", "Bearer wrong")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isUnauthorized)
        }

        @Test
        fun `should blacklist jwt`() {
            val (user, token) = dataGenerator.generateUser()
            mvc
                .perform(
                    get("/api/v1/users/me")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.username").value(user.username))
            mvc
                .perform(
                    post("/api/v1/users/logout")
                        .queryParam("token", token)
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isOk)
            mvc
                .perform(
                    post("/api/v1/users/logout")
                        .queryParam("token", token)
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isUnauthorized)
            mvc
                .perform(
                    get("/api/v1/users/me")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isUnauthorized)
        }
    }
