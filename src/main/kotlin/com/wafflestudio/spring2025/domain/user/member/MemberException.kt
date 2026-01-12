package com.wafflestudio.spring2025.domain.user.member

import com.wafflestudio.spring2025.common.exception.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class MemberException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class MemberNotFoundException : MemberException(
    errorCode = 0,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "Member not found",
)

class MemberAlreadyExistsException : MemberException(
    errorCode = 0,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "Member already exists in this group",
)

class MemberUnauthorizedException : MemberException(
    errorCode = 0,
    httpStatusCode = HttpStatus.FORBIDDEN,
    msg = "Not authorized to modify this member",
)

class InvalidMemberRoleException : MemberException(
    errorCode = 0,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Invalid member role",
)