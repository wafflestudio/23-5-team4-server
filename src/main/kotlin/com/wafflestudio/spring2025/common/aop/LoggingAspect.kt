package com.wafflestudio.spring2025.common.aop

import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggingAspect {
    private val log = LoggerFactory.getLogger(javaClass)

    @AfterThrowing(
        pointcut = "within(com.wafflestudio.spring2025.domain..service..*)",
        throwing = "ex",
    )
    fun logException(ex: Throwable) {
        log.error("Unhandled exception in service", ex)
    }
}
