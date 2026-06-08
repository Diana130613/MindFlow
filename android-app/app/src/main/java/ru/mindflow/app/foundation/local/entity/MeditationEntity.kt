package ru.mindflow.app.foundation.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meditations")
data class MeditationEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val description: String?,
    val durationMinutes: Int?,
    val audioUrl: String?,
    val imageUrl: String?,
    val difficultyLevel: String?,
    val categoryName: String?
)
