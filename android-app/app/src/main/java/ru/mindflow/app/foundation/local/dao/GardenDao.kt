package ru.mindflow.app.foundation.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.mindflow.app.foundation.local.entity.GardenEntity

@Dao
interface GardenDao {

    @Query("SELECT * FROM garden WHERE id = 1")
    fun observe(): Flow<GardenEntity?>

    @Query("SELECT * FROM garden WHERE id = 1")
    suspend fun get(): GardenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: GardenEntity)
}