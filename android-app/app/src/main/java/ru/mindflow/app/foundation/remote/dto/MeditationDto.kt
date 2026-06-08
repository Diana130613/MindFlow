package ru.mindflow.app.foundation.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MeditationDto(
    val id: Long,
    val title: String,
    val description: String?,
    val durationMinutes: Int?,
    val audioUrl: String?,
    val imageUrl: String?,
    val difficultyLevel: String?,
    val categoryName: String?
)
