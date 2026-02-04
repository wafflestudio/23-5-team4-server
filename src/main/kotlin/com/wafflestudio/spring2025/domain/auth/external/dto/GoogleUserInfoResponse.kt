package com.wafflestudio.spring2025.domain.auth.external.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleUserInfoResponse(
    val id: String,
    val email: String,
    @JsonProperty("verified_email")
    val verifiedEmail: Boolean,
    val name: String,
    @JsonProperty("given_name")
    val givenName: String,
    @JsonProperty("family_name")
    val familyName: String?,
    val picture: String,
)
