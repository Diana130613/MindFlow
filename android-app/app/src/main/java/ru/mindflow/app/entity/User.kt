package ru.mindflow.app.entity

data class User(
    val email: String,
    val name: String,
    val role: String = "ROLE_USER"
) {
    fun isAdmin(): Boolean = role == "ROLE_ADMIN"
    fun displayName(): String = name.ifBlank { email }
}
