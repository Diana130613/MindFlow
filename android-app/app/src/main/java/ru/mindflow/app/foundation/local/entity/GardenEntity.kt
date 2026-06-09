package ru.mindflow.app.foundation.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "garden")
data class GardenEntity(
    @PrimaryKey val id: Int = 1,
    val treeLevel: Int = 1,
    val flowersCount: Int = 0,
    val totalMeditationMinutes: Long = 0L,
    val currentStreakDays: Int = 0,
    val lastPracticeDate: String = "",
    /** Comma-separated decoration keys, e.g. "stones,bench" */
    val unlockedDecorations: String = ""
)