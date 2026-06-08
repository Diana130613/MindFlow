package ru.mindflow.app.entity

data class Meditation(
    val id: Long,
    val title: String,
    val description: String?,
    val durationMinutes: Int?,
    val audioUrl: String?,
    val imageUrl: String?,
    val difficultyLevel: String?,
    val categoryName: String?
) {
    fun formattedDuration(): String =
        durationMinutes?.let { "$it мин" } ?: "—"

    fun difficultyLabel(): String = when (difficultyLevel?.uppercase()) {
        "BEGINNER"     -> "Начинающий"
        "INTERMEDIATE" -> "Средний"
        "ADVANCED"     -> "Продвинутый"
        else           -> difficultyLevel ?: "—"
    }
}
