package com.wafflestudio.spring2025.registration

import com.wafflestudio.spring2025.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class RegistrationException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class RegistrationNotFoundException : RegistrationException(
    errorCode = 0,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "Registration not found",
)

class RegistrationAlreadyExistsException : RegistrationException(
    errorCode = 0,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "Registration already exists",
)

class RegistrationUnauthorizedException : RegistrationException(
    errorCode = 0,
    httpStatusCode = HttpStatus.FORBIDDEN,
    msg = "Not authorized to manage registrations",
)
