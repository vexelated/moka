package com.capstoneapps.moka.data.response
data class LoginResponse(
    val status: Int? = null,
    val message: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null
)

