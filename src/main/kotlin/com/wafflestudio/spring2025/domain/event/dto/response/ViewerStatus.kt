package com.wafflestudio.spring2025.domain.event.dto.response

/**
 * 이벤트 상세 조회 시,
 * "viewer(요청자)"가 해당 이벤트와 어떤 관계에 있는지를 나타내는 상태
 */
enum class ViewerStatus {
    HOST,

    CONFIRMED,

    WAITLISTED,

    BANNED,

    NONE,
}
