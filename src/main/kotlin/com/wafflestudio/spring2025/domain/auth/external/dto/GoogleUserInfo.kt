package com.wafflestudio.spring2025.domain.auth.external.dto

data class GoogleUserInfo(
    val id: String,
    val email: String,
    val verifiedEmail: String,
    val name: String,
    val givenName: String,
    val familyName: String,
    val picture: String,
    val locale: String,
)
