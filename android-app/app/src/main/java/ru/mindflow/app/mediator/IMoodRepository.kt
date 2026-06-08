package ru.mindflow.app.mediator

import kotlinx.coroutines.flow.Flow
import ru.mindflow.app.entity.MoodEntry

interface IMoodRepository {
    fun getHistory(): Flow<List<MoodEntry>>
    suspend fun getTodayEntry(): MoodEntry?
    suspend fun save(score: Int, note: String?): MoodEntry
    suspend fun delete(id: Long)
    suspend fun getAverage(days: Int = 30): Double
    suspend fun syncPending()
}
