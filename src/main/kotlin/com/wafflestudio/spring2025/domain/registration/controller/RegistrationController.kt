package com.wafflestudio.spring2025.domain.registration.controller

import com.wafflestudio.spring2025.domain.auth.LoggedInUser
import com.wafflestudio.spring2025.domain.registration.dto.request.UpdateRegistrationStatusRequest
import com.wafflestudio.spring2025.domain.registration.dto.response.DeleteRegistrationResponse
import com.wafflestudio.spring2025.domain.registration.dto.response.GetRegistrationResponse
import com.wafflestudio.spring2025.domain.registration.dto.response.PatchRegistrationResponse
import com.wafflestudio.spring2025.domain.registration.exception.RegistrationErrorCode
import com.wafflestudio.spring2025.domain.registration.exception.RegistrationValidationException
import com.wafflestudio.spring2025.domain.registration.service.RegistrationService
import com.wafflestudio.spring2025.domain.user.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/registrations/{registrationId}")
class RegistrationController(
    private val registrationService: RegistrationService,
) {
    @PatchMapping
    fun updateStatus(
        @PathVariable registrationId: String,
        @RequestBody request: UpdateRegistrationStatusRequest,
        @LoggedInUser user: User?,
    ): ResponseEntity<PatchRegistrationResponse> {
        val status = request.status ?: throw RegistrationValidationException(RegistrationErrorCode.REGISTRATION_INVALID_STATUS)
        val response = registrationService.updateStatus(user?.id, registrationId, status)

        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getRegistrationInformation(
        @PathVariable registrationId: String,
        @LoggedInUser user: User?,
    ): ResponseEntity<GetRegistrationResponse> {
        val response = registrationService.getRegistrationInformation(registrationId, user)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping
    fun delete(
        @PathVariable registrationId: String,
        @LoggedInUser user: User?,
    ): ResponseEntity<DeleteRegistrationResponse> {
        val response =
            registrationService.delete(
                registrationPublicId = registrationId,
                userId = user?.id,
            )

        return ResponseEntity.ok(response)
    }
}
