package ru.mindflow.app.mediator

import kotlinx.coroutines.flow.Flow
import ru.mindflow.app.foundation.local.entity.GardenEntity

interface IGardenRepository {
    fun observe(): Flow<GardenEntity?>
    suspend fun get(): GardenEntity
    suspend fun save(entity: GardenEntity)
}