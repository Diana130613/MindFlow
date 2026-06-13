package ru.mindflow.app.mediator

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.mindflow.app.entity.MoodEntry
import ru.mindflow.app.foundation.local.dao.MoodEntryDao
import ru.mindflow.app.foundation.local.entity.MoodEntryEntity
import ru.mindflow.app.foundation.remote.api.MindFlowApi
import ru.mindflow.app.foundation.remote.dto.MoodEntryDto
import ru.mindflow.app.foundation.remote.dto.MoodEntryRequest

class MoodRepositoryImpl(
    private val api: MindFlowApi,
    private val dao: MoodEntryDao
) : IMoodRepository {

    override fun getHistory(): Flow<List<MoodEntry>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getTodayEntry(): MoodEntry? =
        dao.getLatest()?.toDomain()

    override suspend fun save(score: Int, note: String?): MoodEntry {
        val tempId = System.currentTimeMillis()
        val label = moodLabel(score)
        val localEntry = MoodEntryEntity(
            id = tempId, score = score, note = note,
            moodLabel = label, recordedAt = "", syncPending = true
        )
        dao.upsert(localEntry)

        return runCatching {
            val dto = api.saveMood(MoodEntryRequest(score, note))
            dao.deleteById(tempId)
            val synced = dto.toEntity(syncPending = false)
            dao.upsert(synced)
            synced.toDomain()
        }.getOrDefault(localEntry.toDomain())
    }

    override suspend fun delete(id: Long) {
        dao.deleteById(id)
        runCatching { api.deleteMood(id) }
    }

    override suspend fun getAverage(days: Int): Double =
        runCatching { api.getMoodAverage(days) }
            .getOrElse { dao.getAverageScore() ?: 0.0 }

    override suspend fun syncPending() {
        val pending = dao.getPending()
        pending.forEach { entity ->
            runCatching {
                val dto = api.saveMood(MoodEntryRequest(entity.score, entity.note))
                dao.deleteById(entity.id)
                dao.upsert(dto.toEntity(syncPending = false))
            }
        }
    }

    // ── Data Mappers ──────────────────────────────────────────────────

    private fun MoodEntryEntity.toDomain() = MoodEntry(
        id = id, score = score, note = note,
        moodLabel = moodLabel, recordedAt = recordedAt, syncPending = syncPending
    )

    private fun MoodEntryDto.toEntity(syncPending: Boolean = false) = MoodEntryEntity(
        id = id, score = score, note = note,
        moodLabel = moodLabel, recordedAt = recordedAt, syncPending = syncPending
    )

    private fun moodLabel(score: Int) = when (score) {
        1, 2  -> "Очень плохо"
        3, 4  -> "Плохо"
        5, 6  -> "Нейтрально"
        7, 8  -> "Хорошо"
        9, 10 -> "Отлично"
        else  -> "Нейтрально"
    }
}
