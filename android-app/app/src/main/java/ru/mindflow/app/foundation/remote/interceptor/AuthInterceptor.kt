package ru.mindflow.app.foundation.remote.interceptor

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import ru.mindflow.app.foundation.local.TokenManager

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenManager.getAccessToken() }
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
