package com.wafflestudio.spring2025.domain.registration.controller

import com.wafflestudio.spring2025.domain.auth.LoggedInUser
import com.wafflestudio.spring2025.domain.registration.dto.request.CreateRegistrationRequest
import com.wafflestudio.spring2025.domain.registration.dto.response.CreateRegistrationResponse
import com.wafflestudio.spring2025.domain.registration.dto.response.GetEventRegistrationsResponse
import com.wafflestudio.spring2025.domain.registration.service.RegistrationService
import com.wafflestudio.spring2025.domain.user.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/events/{eventId}/registrations")
class EventRegistrationController(
    private val registrationService: RegistrationService,
) {
    @PostMapping
    fun create(
        @PathVariable eventId: String,
        @RequestBody request: CreateRegistrationRequest,
        @LoggedInUser user: User?,
    ): ResponseEntity<CreateRegistrationResponse> {
        val registration =
            registrationService.create(
                eventId = eventId,
                userId = user?.id,
                guestName = request.guestName,
                guestEmail = request.guestEmail,
            )
        return ResponseEntity.ok(registration)
    }

    @GetMapping
    fun list(
        @PathVariable eventId: String,
        @LoggedInUser user: User?,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) orderBy: String?,
        @RequestParam(required = false) cursor: Int?,
    ): ResponseEntity<GetEventRegistrationsResponse> =
        ResponseEntity.ok(
            registrationService.getEventRegistration(
                eventId = eventId,
                requesterId = user?.id,
                status = status,
                orderBy = orderBy,
                cursor = cursor,
            ),
        )
}
