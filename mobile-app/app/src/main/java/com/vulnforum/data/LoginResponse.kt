package com.vulnforum.data

data class LoginResponse(
    val token: String,
    val role: String,
    val username: String,
    var balance: Float
)