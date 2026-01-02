package com.wafflestudio.spring2025.event.service

import com.wafflestudio.spring2025.event.dto.core.EventDto
import com.wafflestudio.spring2025.event.repository.EventRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class EventService(
    private val eventRepository: EventRepository,
) {
    fun create(
        groupId: Long,
        startTime: Instant,
        endTime: Instant,
        registerDeadline: Instant,
        detail: String,
        capacity: Int,
    ): EventDto {
        TODO("일정 생성 구현")
    }

    fun getById(eventId: Long): EventDto {
        TODO("일정 조회 구현")
    }

    fun getByGroupId(groupId: Long): List<EventDto> {
        TODO("동아리별 일정 목록 조회 구현")
    }

    fun getUpcomingEvents(groupId: Long, limit: Int = 3): List<EventDto> {
        TODO("다가오는 일정 조회 구현")
    }

    fun update(
        eventId: Long,
        startTime: Instant?,
        endTime: Instant?,
        registerDeadline: Instant?,
        detail: String?,
        capacity: Int?,
    ): EventDto {
        TODO("일정 수정 구현")
    }

    fun delete(eventId: Long) {
        TODO("일정 삭제 구현")
    }

    fun registerUser(eventId: Long, userId: Long) {
        TODO("일정 참가 신청 구현")
    }

    fun unregisterUser(eventId: Long, userId: Long) {
        TODO("일정 참가 취소 구현")
    }
}
