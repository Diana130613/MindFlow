package ru.mindflow.app.foundation.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.mindflow.app.foundation.local.entity.MeditationEntity

@Dao
interface MeditationDao {

    @Query("SELECT * FROM meditations ORDER BY title ASC")
    fun getAll(): Flow<List<MeditationEntity>>

    @Query("SELECT * FROM meditations WHERE id = :id")
    suspend fun getById(id: Long): MeditationEntity?

    @Query("SELECT * FROM meditations WHERE title LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<MeditationEntity>>

    @Upsert
    suspend fun upsertAll(items: List<MeditationEntity>)

    @Query("DELETE FROM meditations")
    suspend fun clearAll()
}
