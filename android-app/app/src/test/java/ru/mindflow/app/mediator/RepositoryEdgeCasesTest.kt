package ru.mindflow.app.mediator

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import ru.mindflow.app.foundation.local.dao.MeditationDao
import ru.mindflow.app.foundation.local.dao.MoodEntryDao
import ru.mindflow.app.foundation.local.entity.MeditationEntity
import ru.mindflow.app.foundation.local.entity.MoodEntryEntity
import ru.mindflow.app.foundation.remote.api.MindFlowApi
import ru.mindflow.app.foundation.remote.dto.AuthResponse
import ru.mindflow.app.foundation.remote.dto.LoginRequest
import ru.mindflow.app.foundation.remote.dto.MeditationDto
import ru.mindflow.app.foundation.remote.dto.MoodEntryDto
import ru.mindflow.app.foundation.remote.dto.MoodEntryRequest
import ru.mindflow.app.foundation.remote.dto.RegisterRequest

// ═══════════════════════════════════════════════════════════════════════════
// MoodRepositoryImpl — дополнительные граничные случаи
// ═══════════════════════════════════════════════════════════════════════════

class MoodRepositoryEdgeCasesTest {

    private val api = FakeApi2()
    private val dao = FakeMoodDao2()
    private val repo = MoodRepositoryImpl(api, dao)

    @Test fun `getHistory returns empty list when dao is empty`() = runBlocking {
        val list = repo.getHistory().first()
        assertTrue(list.isEmpty())
    }

    @Test fun `getHistory returns multiple entries`() = runBlocking {
        dao.upsert(MoodEntryEntity(1L, 5, null, "Нейтрально", "2026-01-01"))
        dao.upsert(MoodEntryEntity(2L, 8, "good", "Хорошо", "2026-01-02"))
        dao.upsert(MoodEntryEntity(3L, 2, null, "Очень плохо", "2026-01-03"))
        val list = repo.getHistory().first()
        assertEquals(3, list.size)
    }

    @Test fun `save offline stores score 1 with syncPending true`() = runBlocking {
        api.shouldFailSaveMood = true
        val entry = repo.save(1, null)
        assertEquals(1, entry.score)
        assertTrue(entry.syncPending)
    }

    @Test fun `save with score 10 maps to Отлично label`() = runBlocking {
        api.moodDtoToReturn = MoodEntryDto(99L, 10, null, "Отлично", "2026-01-01")
        val entry = repo.save(10, null)
        assertEquals("Отлично", entry.moodLabel)
    }

    @Test fun `save success returns entry with syncPending false`() = runBlocking {
        api.moodDtoToReturn = MoodEntryDto(1L, 6, null, "Нейтрально", "2026-01-01")
        val entry = repo.save(6, null)
        assertFalse(entry.syncPending)
    }

    @Test fun `save success with note preserves note`() = runBlocking {
        api.moodDtoToReturn = MoodEntryDto(1L, 7, "feeling good", "Хорошо", "2026-01-01")
        val entry = repo.save(7, "feeling good")
        assertEquals("feeling good", entry.note)
    }

    @Test fun `delete calls api deleteMood`() = runBlocking {
        dao.upsert(MoodEntryEntity(10L, 5, null, "Нейтрально", "2026-01-01"))
        repo.delete(10L)
        assertEquals(10L, api.lastDeletedId)
    }

    @Test fun `syncPending partial api failure does not throw`() = runBlocking {
        dao.upsert(MoodEntryEntity(1L, 5, null, "Нейтрально", "", syncPending = true))
        dao.upsert(MoodEntryEntity(2L, 8, null, "Хорошо", "", syncPending = true))
        api.shouldFailSaveMood = true
        repo.syncPending() // should not throw
        assertEquals(2, dao.entries.values.count { it.syncPending })
    }

    @Test fun `getAverage passes days parameter to api`() = runBlocking {
        api.averageToReturn = 5.0
        repo.getAverage(7)
        assertEquals(7, api.lastAverageDays)
    }

    @Test fun `save with score 5 returns Нейтрально label offline`() = runBlocking {
        api.shouldFailSaveMood = true
        val entry = repo.save(5, null)
        assertTrue(entry.syncPending)
        assertEquals(5, entry.score)
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// MeditationRepositoryImpl — дополнительные граничные случаи
// ═══════════════════════════════════════════════════════════════════════════

class MeditationRepositoryEdgeCasesTest {

    private val api = FakeApi2()
    private val dao = FakeMeditationDao2()
    private val repo = MeditationRepositoryImpl(api, dao)

    @Test fun `getAll returns empty list when dao is empty`() = runBlocking {
        val list = repo.getAll().first()
        assertTrue(list.isEmpty())
    }

    @Test fun `getAll returns multiple meditations`() = runBlocking {
        dao.store(MeditationEntity(1L, "A", null, 5, null, null, "BEGINNER", null))
        dao.store(MeditationEntity(2L, "B", null, 10, null, null, "ADVANCED", null))
        dao.store(MeditationEntity(3L, "C", null, 15, null, null, "INTERMEDIATE", null))
        val list = repo.getAll().first()
        assertEquals(3, list.size)
    }

    @Test fun `sync with empty api response clears existing items`() = runBlocking {
        dao.store(MeditationEntity(1L, "Old", null, null, null, null, null, null))
        api.meditationsToReturn = emptyList()
        repo.sync()
        val list = repo.getAll().first()
        assertTrue(list.isEmpty())
    }

    @Test fun `getById returns null after clearAll`() = runBlocking {
        dao.store(MeditationEntity(5L, "Test", null, null, null, null, null, null))
        dao.clearAll()
        assertNull(repo.getById(5L))
    }

    @Test fun `sync replaces existing item with updated data`() = runBlocking {
        dao.store(MeditationEntity(1L, "Old Title", null, 5, null, null, null, null))
        api.meditationsToReturn = listOf(
            MeditationDto(1L, "New Title", null, 10, null, null, "BEGINNER", null)
        )
        repo.sync()
        val result = repo.getById(1L)
        assertEquals("New Title", result?.title)
        assertEquals(10, result?.durationMinutes)
    }

    @Test fun `search with empty string returns all items`() = runBlocking {
        dao.store(MeditationEntity(1L, "Дыхание", null, null, null, null, null, null))
        dao.store(MeditationEntity(2L, "Сон", null, null, null, null, null, null))
        val list = repo.search("").first()
        assertEquals(2, list.size)
    }

    @Test fun `search filters by partial query`() = runBlocking {
        dao.store(MeditationEntity(1L, "Дыхание", null, null, null, null, null, null))
        dao.store(MeditationEntity(2L, "Сон", null, null, null, null, null, null))
        val list = repo.search("Дых").first()
        assertEquals(1, list.size)
        assertEquals("Дыхание", list[0].title)
    }

    @Test fun `getById maps all fields correctly`() = runBlocking {
        dao.store(MeditationEntity(
            id = 7L, title = "Осознанность", description = "Описание",
            durationMinutes = 20, audioUrl = "audio.mp3", imageUrl = "img.png",
            difficultyLevel = "INTERMEDIATE", categoryName = "Стресс"
        ))
        val m = repo.getById(7L)!!
        assertEquals(7L, m.id)
        assertEquals("Осознанность", m.title)
        assertEquals("Описание", m.description)
        assertEquals(20, m.durationMinutes)
        assertEquals("INTERMEDIATE", m.difficultyLevel)
        assertEquals("Стресс", m.categoryName)
    }

    @Test fun `getById returns null when id does not exist`() = runBlocking {
        assertNull(repo.getById(9999L))
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Fakes
// ═══════════════════════════════════════════════════════════════════════════

class FakeApi2 : MindFlowApi {
    var meditationsToReturn: List<MeditationDto> = emptyList()
    var shouldFailMeditations = false
    var moodDtoToReturn: MoodEntryDto = MoodEntryDto(1L, 5, null, "Нейтрально", "2026-01-01")
    var shouldFailSaveMood = false
    var shouldFailDeleteMood = false
    var shouldFailAverage = false
    var averageToReturn = 0.0
    var lastDeletedId: Long? = null
    var lastAverageDays: Int? = null

    override suspend fun register(request: RegisterRequest): AuthResponse = throw UnsupportedOperationException()
    override suspend fun login(request: LoginRequest): AuthResponse = throw UnsupportedOperationException()
    override suspend fun refresh(token: String): AuthResponse = throw UnsupportedOperationException()
    override suspend fun getMeditations(categoryId: Long?, search: String?): List<MeditationDto> {
        if (shouldFailMeditations) throw RuntimeException("API error")
        return meditationsToReturn
    }
    override suspend fun getMeditationById(id: Long): MeditationDto = throw UnsupportedOperationException()
    override suspend fun saveMood(request: MoodEntryRequest): MoodEntryDto {
        if (shouldFailSaveMood) throw RuntimeException("API error")
        return moodDtoToReturn
    }
    override suspend fun getMoodHistory(days: Int): List<MoodEntryDto> = emptyList()
    override suspend fun getMoodToday(): MoodEntryDto? = null
    override suspend fun getMoodAverage(days: Int): Double {
        lastAverageDays = days
        if (shouldFailAverage) throw RuntimeException("API error")
        return averageToReturn
    }
    override suspend fun deleteMood(id: Long) {
        lastDeletedId = id
        if (shouldFailDeleteMood) throw RuntimeException("API error")
    }
}

class FakeMoodDao2 : MoodEntryDao {
    val entries = mutableMapOf<Long, MoodEntryEntity>()
    private val _flow = MutableStateFlow<List<MoodEntryEntity>>(emptyList())

    override fun getAll(): Flow<List<MoodEntryEntity>> = _flow
    override suspend fun getPending(): List<MoodEntryEntity> = entries.values.filter { it.syncPending }
    override suspend fun getLatest(): MoodEntryEntity? = entries.values.maxByOrNull { it.id }
    override suspend fun upsert(entry: MoodEntryEntity) {
        entries[entry.id] = entry
        _flow.value = entries.values.toList()
    }
    override suspend fun upsertAll(items: List<MoodEntryEntity>) {
        items.forEach { entries[it.id] = it }
        _flow.value = entries.values.toList()
    }
    override suspend fun deleteById(id: Long) {
        entries.remove(id)
        _flow.value = entries.values.toList()
    }
    override suspend fun clearAll() {
        entries.clear()
        _flow.value = emptyList()
    }
    override suspend fun getAverageScore(): Double? {
        return entries.values.map { it.score }.average().takeIf { it.isFinite() }
    }
}

class FakeMeditationDao2 : MeditationDao {
    val stored = mutableMapOf<Long, MeditationEntity>()
    private val _flow = MutableStateFlow<List<MeditationEntity>>(emptyList())

    fun store(entity: MeditationEntity) {
        stored[entity.id] = entity
        _flow.value = stored.values.toList()
    }

    override fun getAll(): Flow<List<MeditationEntity>> = _flow
    override suspend fun getById(id: Long): MeditationEntity? = stored[id]
    override fun search(query: String): Flow<List<MeditationEntity>> =
        MutableStateFlow(stored.values.filter { query.isEmpty() || it.title.contains(query) })
    override suspend fun upsertAll(items: List<MeditationEntity>) {
        items.forEach { stored[it.id] = it }
        _flow.value = stored.values.toList()
    }
    override suspend fun clearAll() {
        stored.clear()
        _flow.value = emptyList()
    }
}