package com.wafflestudio.spring2025.domain.registration.controller

import com.wafflestudio.spring2025.domain.auth.AuthRequired
import com.wafflestudio.spring2025.domain.auth.LoggedInUser
import com.wafflestudio.spring2025.domain.registration.dto.response.GetMyRegistrationsResponse
import com.wafflestudio.spring2025.domain.registration.service.RegistrationService
import com.wafflestudio.spring2025.domain.user.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@AuthRequired
@RestController
@RequestMapping("/api/registrations")
class MyRegistrationController(
    private val registrationService: RegistrationService,
) {
    @GetMapping("/me")
    fun myRegistrations(
        @LoggedInUser user: User,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<GetMyRegistrationsResponse> {
        val userId = requireNotNull(user.id) { "로그인 사용자 ID가 없습니다." }
        return ResponseEntity.ok(registrationService.getMyRegistrations(userId, page, size))
    }
}
