package com.wafflestudio.spring2025.domain.event.controller

import com.wafflestudio.spring2025.domain.auth.AuthRequired
import com.wafflestudio.spring2025.domain.auth.LoggedInUser
import com.wafflestudio.spring2025.domain.event.dto.request.CreateEventRequest
import com.wafflestudio.spring2025.domain.event.dto.request.UpdateEventRequest
import com.wafflestudio.spring2025.domain.event.dto.response.CreateEventResponse
import com.wafflestudio.spring2025.domain.event.dto.response.EventDetailResponse
import com.wafflestudio.spring2025.domain.event.dto.response.MyEventsInfiniteResponse
import com.wafflestudio.spring2025.domain.event.dto.response.UpdateEventResponse
import com.wafflestudio.spring2025.domain.event.service.EventService
import com.wafflestudio.spring2025.domain.user.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService,
) {
    @AuthRequired
    @PostMapping // POST /api/events
    fun create(
        @LoggedInUser user: User,
        @RequestBody request: CreateEventRequest,
    ): ResponseEntity<CreateEventResponse> {
        val publicId: String =
            eventService.create(
                title = request.title,
                description = request.description,
                location = request.location,
                startsAt = request.startsAt,
                endsAt = request.endsAt,
                capacity = request.capacity,
                registrationStartsAt = request.registrationStartsAt,
                registrationEndsAt = request.registrationEndsAt,
                waitlistEnabled = request.waitlistEnabled,
                createdBy = user.id!!,
            )

        return ResponseEntity.ok(CreateEventResponse(publicId))
    }

    @GetMapping("/{publicId}") // GET /api/events/{publicId}
    fun getById(
        @LoggedInUser user: User?, // nullable
        @PathVariable publicId: String,
    ): ResponseEntity<EventDetailResponse> {
        val response =
            eventService.getDetail(
                publicId = publicId,
                requesterId = user?.id, // Long?
            )
        return ResponseEntity.ok(response)
    }

    @AuthRequired
    @GetMapping("/me")
    fun getMyEvents(
        @LoggedInUser user: User,
        @RequestParam(required = false) cursor: Instant?,
        @RequestParam(defaultValue = "5") size: Int,
    ): ResponseEntity<MyEventsInfiniteResponse> {
        val response =
            eventService.getMyEventsInfinite(
                createdBy = user.id!!,
                cursor = cursor,
                size = size,
            )
        return ResponseEntity.ok(response)
    }

    @AuthRequired
    @PutMapping("/{publicId}") // PUT /api/events/{publicId}
    fun update(
        @LoggedInUser user: User,
        @PathVariable publicId: String,
        @RequestBody request: UpdateEventRequest,
    ): ResponseEntity<UpdateEventResponse> {
        val event =
            eventService.update(
                publicId = publicId,
                title = request.title,
                description = request.description,
                location = request.location,
                startsAt = request.startsAt,
                endsAt = request.endsAt,
                capacity = request.capacity,
                waitlistEnabled = request.waitlistEnabled,
                registrationStartsAt = request.registrationStartsAt,
                registrationEndsAt = request.registrationEndsAt,
                requesterId = user.id!!,
            )

        val response = UpdateEventResponse.from(event)
        return ResponseEntity.ok(response)
    }

    @AuthRequired
    @DeleteMapping("/{publicId}") // DELETE /api/events/{publicId}
    fun delete(
        @LoggedInUser user: User,
        @PathVariable publicId: String,
    ): ResponseEntity<Unit> {
        eventService.delete(
            publicId = publicId,
            requesterId = user.id!!,
        )
        return ResponseEntity.noContent().build()
    }
}
