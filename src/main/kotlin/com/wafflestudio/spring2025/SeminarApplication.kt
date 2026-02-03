package com.wafflestudio.spring2025

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class SeminarApplication

fun main(args: Array<String>) {
    runApplication<SeminarApplication>(*args)
}
