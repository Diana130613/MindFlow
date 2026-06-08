package ru.mindflow.app.foundation.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntryEntity(
    @PrimaryKey val id: Long,
    val score: Int,
    val note: String?,
    val moodLabel: String,
    val recordedAt: String,
    val syncPending: Boolean = false
)
