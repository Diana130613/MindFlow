package ru.mindflow.app.mediator

import kotlinx.coroutines.flow.Flow
import ru.mindflow.app.entity.Meditation

interface IMeditationRepository {
    fun getAll(): Flow<List<Meditation>>
    fun search(query: String): Flow<List<Meditation>>
    suspend fun getById(id: Long): Meditation?
    suspend fun sync()
}
