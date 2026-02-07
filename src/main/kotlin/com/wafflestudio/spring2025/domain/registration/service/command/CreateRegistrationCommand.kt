package com.wafflestudio.spring2025.domain.registration.service.command

sealed class CreateRegistrationCommand {
    data class Member(
        val userId: Long,
        val eventId: String,
    ) : CreateRegistrationCommand()

    data class Guest(
        val eventId: String,
        val name: String,
        val email: String,
    ) : CreateRegistrationCommand()
}
