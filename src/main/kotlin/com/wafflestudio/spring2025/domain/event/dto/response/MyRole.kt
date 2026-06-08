package com.wafflestudio.spring2025.domain.event.dto.response

/**
 * 요청자 역할
 * - CREATOR: 이벤트 생성자
 * - PARTICIPANT: 이벤트 참여자 (CONFIRMED / WAITLISTED)
 * - NONE: 로그인은 했지만 참여하지 않음
 */
enum class MyRole {
    CREATOR,

    PARTICIPANT,

    NONE,
}
