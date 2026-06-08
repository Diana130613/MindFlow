package ru.mindflow.app.mediator

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.mindflow.app.entity.Meditation
import ru.mindflow.app.foundation.local.dao.MeditationDao
import ru.mindflow.app.foundation.local.entity.MeditationEntity
import ru.mindflow.app.foundation.remote.api.MindFlowApi
import ru.mindflow.app.foundation.remote.dto.MeditationDto

class MeditationRepositoryImpl(
    private val api: MindFlowApi,
    private val dao: MeditationDao
) : IMeditationRepository {

    override fun getAll(): Flow<List<Meditation>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun search(query: String): Flow<List<Meditation>> =
        dao.search(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): Meditation? =
        dao.getById(id)?.toDomain()

    override suspend fun sync() {
        runCatching {
            val dtos = api.getMeditations()
            dao.clearAll()
            dao.upsertAll(dtos.map { it.toEntity() })
        }
    }

    // ── Data Mappers ──────────────────────────────────────────────────

    private fun MeditationEntity.toDomain() = Meditation(
        id = id, title = title, description = description,
        durationMinutes = durationMinutes, audioUrl = audioUrl,
        imageUrl = imageUrl, difficultyLevel = difficultyLevel,
        categoryName = categoryName
    )

    private fun MeditationDto.toEntity() = MeditationEntity(
        id = id, title = title, description = description,
        durationMinutes = durationMinutes, audioUrl = audioUrl,
        imageUrl = imageUrl, difficultyLevel = difficultyLevel,
        categoryName = categoryName
    )
}
