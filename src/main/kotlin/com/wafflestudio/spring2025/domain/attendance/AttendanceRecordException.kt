package com.wafflestudio.spring2025.domain.attendance

import com.wafflestudio.spring2025.common.exception.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class AttendanceRecordException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class AttendanceRecordNotFoundException : AttendanceRecordException(
    errorCode = 0,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "Attendance record not found",
)

class AttendanceRecordAlreadyExistsException : AttendanceRecordException(
    errorCode = 0,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "Attendance record already exists",
)