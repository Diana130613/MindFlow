package ru.mindflow.app.foundation.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val name: String,
    val role: String
)
