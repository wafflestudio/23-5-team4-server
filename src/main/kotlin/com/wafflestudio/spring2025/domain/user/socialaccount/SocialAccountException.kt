package com.wafflestudio.spring2025.domain.user.socialaccount

import com.wafflestudio.spring2025.common.exception.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class SocialAccountException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class SocialAccountNotFoundException : SocialAccountException(
    errorCode = 0,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "Social account not found",
)

class SocialAccountAlreadyLinkedException : SocialAccountException(
    errorCode = 0,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "Social account already linked",
)

class SocialAccountUnauthorizedException : SocialAccountException(
    errorCode = 0,
    httpStatusCode = HttpStatus.FORBIDDEN,
    msg = "Not authorized to access this social account",
)