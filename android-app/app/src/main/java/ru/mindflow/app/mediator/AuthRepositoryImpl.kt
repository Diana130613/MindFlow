package ru.mindflow.app.mediator

import ru.mindflow.app.entity.User
import ru.mindflow.app.foundation.local.TokenManager
import ru.mindflow.app.foundation.remote.api.MindFlowApi
import ru.mindflow.app.foundation.remote.dto.LoginRequest
import ru.mindflow.app.foundation.remote.dto.RegisterRequest

class AuthRepositoryImpl(
    private val api: MindFlowApi,
    private val tokenManager: TokenManager
) : IAuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        val response = api.login(LoginRequest(email, password))
        tokenManager.saveTokens(response.accessToken, response.refreshToken, response.email, response.name)
        User(response.email, response.name, response.role)
    }

    override suspend fun register(email: String, password: String, name: String): Result<User> = runCatching {
        val response = api.register(RegisterRequest(email, password, name))
        tokenManager.saveTokens(response.accessToken, response.refreshToken, response.email, response.name)
        User(response.email, response.name, response.role)
    }

    override suspend fun logout() {
        tokenManager.clear()
    }

    override suspend fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    override suspend fun getCurrentUser(): User? {
        val email = tokenManager.getUserEmail() ?: return null
        val name  = tokenManager.getUserName() ?: return null
        return User(email, name)
    }
}
