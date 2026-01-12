package com.wafflestudio.spring2025.domain.user.member.service

import com.wafflestudio.spring2025.domain.user.member.dto.core.MemberDto
import com.wafflestudio.spring2025.domain.user.member.model.MemberType
import com.wafflestudio.spring2025.domain.user.member.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {
    fun getByGroupId(groupId: Long): List<MemberDto> {
        TODO("동아리 멤버 목록 조회 (역할 순서대로 정렬) 구현")
    }

    fun getByUserId(userId: Long): List<MemberDto> {
        TODO("사용자가 가입한 동아리 목록 조회 구현")
    }

    fun update(memberId: Long, nickname: String?, type: MemberType?): MemberDto {
        TODO("멤버 정보 수정 구현")
    }

    fun delete(memberId: Long) {
        TODO("멤버 삭제 (동아리 탈퇴) 구현")
    }

    fun getMember(userId: Long, groupId: Long): MemberDto? {
        TODO("특정 사용자의 특정 동아리 멤버 정보 조회 구현")
    }
}