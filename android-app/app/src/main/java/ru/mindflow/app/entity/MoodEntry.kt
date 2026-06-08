package ru.mindflow.app.entity

data class MoodEntry(
    val id: Long,
    val score: Int,
    val note: String?,
    val moodLabel: String,
    val recordedAt: String,
    val syncPending: Boolean = false
) {
    fun emoji(): String = when (score) {
        1, 2  -> "😢"
        3, 4  -> "😕"
        5, 6  -> "😐"
        7, 8  -> "🙂"
        9, 10 -> "😄"
        else  -> "😐"
    }
}
