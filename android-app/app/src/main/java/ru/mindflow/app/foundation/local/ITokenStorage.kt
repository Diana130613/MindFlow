package ru.mindflow.app.foundation.local

interface ITokenStorage {
    suspend fun saveTokens(accessToken: String, refreshToken: String, email: String, name: String)
    suspend fun updateName(name: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun getUserEmail(): String?
    suspend fun getUserName(): String?
    suspend fun getJoinDateMillis(): Long?
    suspend fun isLoggedIn(): Boolean
    suspend fun clear()
}
