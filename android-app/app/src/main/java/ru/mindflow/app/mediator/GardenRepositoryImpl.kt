package ru.mindflow.app.mediator

import kotlinx.coroutines.flow.Flow
import ru.mindflow.app.foundation.local.dao.GardenDao
import ru.mindflow.app.foundation.local.entity.GardenEntity

class GardenRepositoryImpl(
    private val dao: GardenDao
) : IGardenRepository {

    override fun observe(): Flow<GardenEntity?> = dao.observe()

    override suspend fun get(): GardenEntity = dao.get() ?: GardenEntity()

    override suspend fun save(entity: GardenEntity) = dao.save(entity)
}
