package ru.mindflow.app

import android.content.Context
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import ru.mindflow.app.foundation.local.MindFlowDatabase
import ru.mindflow.app.foundation.local.TokenManager
import ru.mindflow.app.foundation.remote.api.MindFlowApi
import ru.mindflow.app.foundation.remote.interceptor.AuthInterceptor
import ru.mindflow.app.mediator.AuthRepositoryImpl
import ru.mindflow.app.mediator.IAuthRepository
import ru.mindflow.app.mediator.IMeditationRepository
import ru.mindflow.app.mediator.IMoodRepository
import ru.mindflow.app.mediator.MeditationRepositoryImpl
import ru.mindflow.app.mediator.MoodRepositoryImpl
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {

    val tokenManager = TokenManager(context)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(tokenManager))
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8081/api/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val api: MindFlowApi = retrofit.create(MindFlowApi::class.java)

    private val db = MindFlowDatabase.getInstance(context)

    val authRepository: IAuthRepository = AuthRepositoryImpl(api, tokenManager)

    val meditationRepository: IMeditationRepository =
        MeditationRepositoryImpl(api, db.meditationDao())

    val moodRepository: IMoodRepository =
        MoodRepositoryImpl(api, db.moodEntryDao())
}
