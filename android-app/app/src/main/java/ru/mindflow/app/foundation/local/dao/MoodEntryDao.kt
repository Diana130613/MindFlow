package ru.mindflow.app.foundation.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.mindflow.app.foundation.local.entity.MoodEntryEntity

@Dao
interface MoodEntryDao {

    @Query("SELECT * FROM mood_entries ORDER BY recordedAt DESC")
    fun getAll(): Flow<List<MoodEntryEntity>>

    @Query("SELECT * FROM mood_entries WHERE syncPending = 1")
    suspend fun getPending(): List<MoodEntryEntity>

    @Query("SELECT * FROM mood_entries ORDER BY recordedAt DESC LIMIT 1")
    suspend fun getLatest(): MoodEntryEntity?

    @Upsert
    suspend fun upsert(entry: MoodEntryEntity)

    @Upsert
    suspend fun upsertAll(entries: List<MoodEntryEntity>)

    @Query("DELETE FROM mood_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT AVG(score) FROM mood_entries")
    suspend fun getAverageScore(): Double?

    @Query("DELETE FROM mood_entries")
    suspend fun clearAll()
}
