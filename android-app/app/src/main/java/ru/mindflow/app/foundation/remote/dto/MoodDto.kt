package ru.mindflow.app.foundation.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MoodEntryRequest(
    val score: Int,
    val note: String? = null
)

@Serializable
data class MoodEntryDto(
    val id: Long,
    val score: Int,
    val note: String?,
    val moodLabel: String,
    val recordedAt: String
)
