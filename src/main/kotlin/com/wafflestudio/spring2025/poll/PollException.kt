package com.wafflestudio.spring2025.poll

import com.wafflestudio.spring2025.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class PollException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class PollNotFoundException : PollException(
    errorCode = 0,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "Poll not found",
)

class PollClosedException : PollException(
    errorCode = 0,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Poll is closed",
)

class PollUnauthorizedException : PollException(
    errorCode = 0,
    httpStatusCode = HttpStatus.FORBIDDEN,
    msg = "Not authorized to modify this poll",
)

class AlreadyVotedException : PollException(
    errorCode = 0,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "Already voted on this poll",
)
