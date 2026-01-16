package com.wafflestudio.spring2025.domain.event.service

import com.wafflestudio.spring2025.domain.event.model.Event
import com.wafflestudio.spring2025.domain.event.dto.response.EventDetailResponse
import com.wafflestudio.spring2025.domain.event.dto.GuestPreview
import com.wafflestudio.spring2025.domain.event.dto.MyRole
import com.wafflestudio.spring2025.domain.event.repository.EventRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class EventService(
    private val eventRepository: EventRepository,
) {

    /**
     * 일정 생성
     * - API 설계상 body를 비우고 201 + Location을 주기 위해 생성된 eventId를 반환
     */
    fun create(
        title: String,
        description: String?,
        location: String?,
        startAt: Instant?,
        endAt: Instant?,
        capacity: Int?,
        waitlistEnabled: Boolean,
        registrationStart: Instant?,
        registrationDeadline: Instant?,
        createdBy: Long,
    ): Long {
        validateCreateOrUpdate(
            title = title,
            startAt = startAt,
            endAt = endAt,
            capacity = capacity,
            registrationStart = registrationStart,
            registrationDeadline = registrationDeadline,
        )

        val event = Event(
            title = title.trim(),
            description = description,
            location = location,
            startAt = startAt,
            endAt = endAt,
            capacity = capacity,
            waitlistEnabled = waitlistEnabled,
            registrationStart = registrationStart,
            registrationDeadline = registrationDeadline,
            createdBy = createdBy,
        )

        val saved = eventRepository.save(event)
        return requireNotNull(saved.id) { "Saved event id is null" }
    }

    /**
     * 일정 상세 조회
     * - registrations 도메인 붙기 전: participants/waiting/guests는 기본값으로 내려줌
     */
    fun getDetail(eventId: Long, requesterId: Long): EventDetailResponse {
        val event: Event = eventRepository.findById(eventId)
            .orElseThrow { NoSuchElementException("Event not found: $eventId") }

        val myRole = when {
            event.createdBy == requesterId -> MyRole.CREATOR
            else -> MyRole.NONE // TODO: registrations 붙으면 PARTICIPANT 판단
        }

        return EventDetailResponse(
            title = event.title,
            description = event.description,
            location = event.location,
            startAt = event.startAt,
            endAt = event.endAt,
            capacity = event.capacity,
            currentParticipants = 0,           // TODO: registrations count로 채우기
            registrationStart = event.registrationStart,
            registrationDeadline = event.registrationDeadline,
            myRole = myRole,
            waitingNum = null,                // TODO: 내 대기 순번 계산
            guestsPreview = emptyList(),       // TODO: 상위 N명(예: 4명) 미리보기
        )
    }

    fun getById(eventId: Long): Event {
        return eventRepository.findById(eventId)
            .orElseThrow { NoSuchElementException("Event not found: $eventId") }
    }

    fun getByCreator(createdBy: Long): List<Event> {
        return eventRepository.findByCreatedByOrderByStartAtDesc(createdBy)
    }

    fun update(
        eventId: Long,
        title: String?,
        description: String?,
        location: String?,
        startAt: Instant?,
        endAt: Instant?,
        capacity: Int?,
        waitlistEnabled: Boolean?,
        registrationStart: Instant?,
        registrationDeadline: Instant?,
    ): Event {
        val event = eventRepository.findById(eventId)
            .orElseThrow { NoSuchElementException("Event not found: $eventId") }

        // null은 "변경 없음"
        title?.let {
            require(it.isNotBlank()) { "title must not be blank" }
            event.title = it.trim()
        }
        description?.let { event.description = it }
        location?.let { event.location = it }
        startAt?.let { event.startAt = it }
        endAt?.let { event.endAt = it }
        capacity?.let { event.capacity = it }
        waitlistEnabled?.let { event.waitlistEnabled = it }
        registrationStart?.let { event.registrationStart = it }
        registrationDeadline?.let { event.registrationDeadline = it }

        // 변경 후에도 도메인 규칙 검증
        validateCreateOrUpdate(
            title = event.title,
            startAt = event.startAt,
            endAt = event.endAt,
            capacity = event.capacity,
            registrationStart = event.registrationStart,
            registrationDeadline = event.registrationDeadline,
        )

        return eventRepository.save(event)
    }

    fun delete(eventId: Long) {
        if (!eventRepository.existsById(eventId)) {
            throw NoSuchElementException("Event not found: $eventId")
        }
        eventRepository.deleteById(eventId)
    }

    private fun validateCreateOrUpdate(
        title: String,
        startAt: Instant?,
        endAt: Instant?,
        capacity: Int?,
        registrationStart: Instant?,
        registrationDeadline: Instant?,
    ) {
        require(title.isNotBlank()) { "title must not be blank" }

        if (startAt != null && endAt != null) {
            require(startAt.isBefore(endAt)) { "startAt must be before endAt" }
        }

        if (capacity != null) {
            require(capacity > 0) { "capacity must be positive" }
        }

        // registrationStart / registrationDeadline 관계
        if (registrationStart != null && registrationDeadline != null) {
            require(!registrationStart.isAfter(registrationDeadline)) {
                "registrationStart must be <= registrationDeadline"
            }
        }

        // registrationDeadline <= startAt (기존 룰 유지)
        if (registrationDeadline != null && startAt != null) {
            require(!registrationDeadline.isAfter(startAt)) {
                "registrationDeadline must be <= startAt"
            }
        }

        // (선택) registrationStart <= startAt 도 같이 강제하면 더 자연스러움
        if (registrationStart != null && startAt != null) {
            require(!registrationStart.isAfter(startAt)) {
                "registrationStart must be <= startAt"
            }
        }
    }
}
