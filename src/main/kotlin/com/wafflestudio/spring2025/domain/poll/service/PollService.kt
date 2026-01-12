package com.wafflestudio.spring2025.domain.poll.service

import com.wafflestudio.spring2025.domain.poll.dto.core.PollDto
import com.wafflestudio.spring2025.domain.poll.repository.PollRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class PollService(
    private val pollRepository: PollRepository,
) {
    fun create(groupId: Long, title: String, deadline: Instant, options: List<String>): PollDto {
        TODO("투표 생성 (선택지 포함) 구현")
    }

    fun getById(pollId: Long): PollDto {
        TODO("투표 조회 구현")
    }

    fun getByGroupId(groupId: Long, onlyActive: Boolean = false): List<PollDto> {
        TODO("동아리별 투표 목록 조회 구현")
    }

    fun vote(pollId: Long, userId: Long, pollOptionId: Long) {
        TODO("투표하기 구현")
    }

    fun closePoll(pollId: Long) {
        TODO("투표 종료 구현")
    }

    fun delete(pollId: Long) {
        TODO("투표 삭제 구현")
    }
}