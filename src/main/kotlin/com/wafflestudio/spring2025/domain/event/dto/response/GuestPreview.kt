package com.wafflestudio.spring2025.domain.event.dto.response

/**
 * 이벤트 참여자 미리보기 정보
 * (회원/비회원 포함)
 */
data class GuestPreview(
    val id: Long?,
    val name: String,
    val profileImage: String?,
)
