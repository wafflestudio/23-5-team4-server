package com.wafflestudio.spring2025.announcement

import com.wafflestudio.spring2025.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class AnnouncementException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class AnnouncementNotFoundException : AnnouncementException(
    errorCode = 0,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "Announcement not found",
)

class AnnouncementUnauthorizedException : AnnouncementException(
    errorCode = 0,
    httpStatusCode = HttpStatus.FORBIDDEN,
    msg = "Not authorized to modify this announcement",
)
