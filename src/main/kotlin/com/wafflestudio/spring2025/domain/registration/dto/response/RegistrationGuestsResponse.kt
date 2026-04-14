package com.wafflestudio.spring2025.domain.registration.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class RegistrationGuestsResponse(
    val guests: List<Guest>,
    @JsonProperty("confirmed_count")
    val confirmedCount: Int,
    @JsonProperty("waiting_count")
    val waitingCount: Int,
) {
    data class Guest(
        val registrationPublicId: String,
        val name: String,
        val email: String?,
        @JsonProperty("profile_image")
        val profileImage: String?,
    )
}
