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
import ru.mindflow.app.foundation.remote.dto.*

// ═══════════════════════════════════════════════════════════════════════════
// MeditationRepositoryImpl tests
// ═══════════════════════════════════════════════════════════════════════════

class MeditationRepositoryImplTest {

    private val api = FakeApi()
    private val dao = FakeMeditationDao()
    private val repo = MeditationRepositoryImpl(api, dao)

    @Test fun `getAll returns dao flow`() = runBlocking {
        dao.store(MeditationEntity(1L, "Дыхание", null, 10, null, null, "BEGINNER", null))
        val list = repo.getAll().first()
        assertEquals(1, list.size)
        assertEquals("Дыхание", list[0].title)
    }

    @Test fun `getById returns null when dao has nothing`() = runBlocking {
        assertNull(repo.getById(99L))
    }

    @Test fun `getById returns mapped meditation`() = runBlocking {
        dao.store(MeditationEntity(5L, "Сон", "Описание", 20, null, null, "INTERMEDIATE", "Сон"))
        val result = repo.getById(5L)
        assertNotNull(result)
        assertEquals(5L, result?.id)
        assertEquals("Сон", result?.title)
        assertEquals("Описание", result?.description)
        assertEquals("INTERMEDIATE", result?.difficultyLevel)
    }

    @Test fun `search returns flow from dao`() = runBlocking {
        dao.store(MeditationEntity(1L, "Дыхание", null, null, null, null, null, null))
        val list = repo.search("Дыхание").first()
        assertEquals(1, list.size)
    }

    @Test fun `sync clears dao and saves api meditations`() = runBlocking {
        api.meditationsToReturn = listOf(
            MeditationDto(1L, "Test1", null, 5, null, null, "BEGINNER", null),
            MeditationDto(2L, "Test2", null, 10, null, null, "ADVANCED", null)
        )
        repo.sync()
        assertEquals(2, dao.stored.size)
        assertTrue(dao.cleared)
    }

    @Test fun `sync on failure does not crash`() = runBlocking {
        api.shouldFailMeditations = true
        repo.sync()
        // no exception thrown
        assertTrue(dao.stored.isEmpty())
    }

    @Test fun `sync maps dto fields to entity correctly`() = runBlocking {
        api.meditationsToReturn = listOf(
            MeditationDto(7L, "Покой", "Описание", 15, "audio.mp3", "img.png", "BEGINNER", "Дыхание")
        )
        repo.sync()
        val saved = dao.stored[7L]
        assertNotNull(saved)
        assertEquals("Покой", saved?.title)
        assertEquals(15, saved?.durationMinutes)
        assertEquals("BEGINNER", saved?.difficultyLevel)
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// MoodRepositoryImpl tests
// ═══════════════════════════════════════════════════════════════════════════

class MoodRepositoryImplTest {

    private val api = FakeApi()
    private val dao = FakeMoodDao()
    private val repo = MoodRepositoryImpl(api, dao)

    @Test fun `getHistory returns dao flow mapped to domain`() = runBlocking {
        dao.upsert(MoodEntryEntity(1L, 7, null, "Хорошо", "2026-01-01"))
        val list = repo.getHistory().first()
        assertEquals(1, list.size)
        assertEquals(7, list[0].score)
        assertEquals("Хорошо", list[0].moodLabel)
    }

    @Test fun `getTodayEntry returns null when dao is empty`() = runBlocking {
        assertNull(repo.getTodayEntry())
    }

    @Test fun `getTodayEntry returns latest mapped entry`() = runBlocking {
        dao.upsert(MoodEntryEntity(10L, 8, "good", "Хорошо", "2026-06-09"))
        val entry = repo.getTodayEntry()
        assertNotNull(entry)
        assertEquals(8, entry?.score)
        assertEquals("good", entry?.note)
    }

    @Test fun `save success stores synced entry and removes temp`() = runBlocking {
        api.moodDtoToReturn = MoodEntryDto(999L, 7, null, "Хорошо", "2026-06-09")
        val entry = repo.save(7, null)
        assertEquals(999L, entry.id)
        assertEquals(7, entry.score)
        assertFalse(entry.syncPending)
        // temp deleted, only synced entry remains
        assertNull(dao.entries.values.find { it.syncPending })
        assertNotNull(dao.entries[999L])
    }

    @Test fun `save failure returns local entry with syncPending true`() = runBlocking {
        api.shouldFailSaveMood = true
        val entry = repo.save(5, "offline")
        assertEquals(5, entry.score)
        assertEquals("offline", entry.note)
        assertTrue(entry.syncPending)
    }

    @Test fun `save applies correct mood label`() = runBlocking {
        api.moodDtoToReturn = MoodEntryDto(1L, 9, null, "Отлично", "2026-01-01")
        val entry = repo.save(9, null)
        assertEquals("Отлично", entry.moodLabel)
    }

    @Test fun `delete removes entry from dao`() = runBlocking {
        dao.upsert(MoodEntryEntity(42L, 5, null, "Нейтрально", "2026-01-01"))
        repo.delete(42L)
        assertNull(dao.entries[42L])
    }

    @Test fun `delete swallows api failure`() = runBlocking {
        api.shouldFailDeleteMood = true
        dao.upsert(MoodEntryEntity(5L, 5, null, "Нейтрально", "2026-01-01"))
        repo.delete(5L)
        assertNull(dao.entries[5L])
    }

    @Test fun `getAverage returns api value`() = runBlocking {
        api.averageToReturn = 7.5
        assertEquals(7.5, repo.getAverage(30), 0.01)
    }

    @Test fun `getAverage returns 0 when api fails`() = runBlocking {
        api.shouldFailAverage = true
        assertEquals(0.0, repo.getAverage(30), 0.0)
    }

    @Test fun `syncPending syncs all pending entries`() = runBlocking {
        dao.upsert(MoodEntryEntity(1L, 6, null, "Нейтрально", "", syncPending = true))
        dao.upsert(MoodEntryEntity(2L, 8, null, "Хорошо", "", syncPending = true))
        api.moodDtoToReturn = MoodEntryDto(100L, 6, null, "Нейтрально", "2026-01-01")
        repo.syncPending()
        // temp entries were removed and synced entry added
        assertNull(dao.entries[1L])
        assertNull(dao.entries[2L])
    }

    @Test fun `syncPending does nothing when no pending entries`() = runBlocking {
        repo.syncPending()
        assertTrue(dao.entries.isEmpty())
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Fakes
// ═══════════════════════════════════════════════════════════════════════════

class FakeApi : MindFlowApi {
    var meditationsToReturn: List<MeditationDto> = emptyList()
    var shouldFailMeditations = false
    var moodDtoToReturn: MoodEntryDto = MoodEntryDto(1L, 5, null, "Нейтрально", "2026-01-01")
    var shouldFailSaveMood = false
    var shouldFailDeleteMood = false
    var shouldFailAverage = false
    var averageToReturn = 0.0

    override suspend fun register(request: RegisterRequest): AuthResponse =
        throw UnsupportedOperationException()
    override suspend fun login(request: LoginRequest): AuthResponse =
        throw UnsupportedOperationException()
    override suspend fun refresh(token: String): AuthResponse =
        throw UnsupportedOperationException()
    override suspend fun getMeditations(categoryId: Long?, search: String?): List<MeditationDto> {
        if (shouldFailMeditations) throw RuntimeException("API error")
        return meditationsToReturn
    }
    override suspend fun getMeditationById(id: Long): MeditationDto =
        throw UnsupportedOperationException()
    override suspend fun saveMood(request: MoodEntryRequest): MoodEntryDto {
        if (shouldFailSaveMood) throw RuntimeException("API error")
        return moodDtoToReturn
    }
    override suspend fun getMoodHistory(days: Int): List<MoodEntryDto> = emptyList()
    override suspend fun getMoodToday(): MoodEntryDto? = null
    override suspend fun getMoodAverage(days: Int): Double {
        if (shouldFailAverage) throw RuntimeException("API error")
        return averageToReturn
    }
    override suspend fun deleteMood(id: Long) {
        if (shouldFailDeleteMood) throw RuntimeException("API error")
    }
}

class FakeMeditationDao : MeditationDao {
    val stored = mutableMapOf<Long, MeditationEntity>()
    var cleared = false
    private val _flow = MutableStateFlow<List<MeditationEntity>>(emptyList())

    fun store(entity: MeditationEntity) {
        stored[entity.id] = entity
        _flow.value = stored.values.toList()
    }

    override fun getAll(): Flow<List<MeditationEntity>> = _flow
    override suspend fun getById(id: Long): MeditationEntity? = stored[id]
    override fun search(query: String): Flow<List<MeditationEntity>> =
        MutableStateFlow(stored.values.filter { it.title.contains(query) })
    override suspend fun upsertAll(items: List<MeditationEntity>) {
        items.forEach { stored[it.id] = it }
        _flow.value = stored.values.toList()
    }
    override suspend fun clearAll() {
        cleared = true
        stored.clear()
        _flow.value = emptyList()
    }
    // Удаляем метод getAverageScore(), так как его нет в интерфейсе MeditationDao
}

class FakeMoodDao : MoodEntryDao {
    val entries = mutableMapOf<Long, MoodEntryEntity>()
    private val _flow = MutableStateFlow<List<MoodEntryEntity>>(emptyList())

    override fun getAll(): Flow<List<MoodEntryEntity>> = _flow
    override suspend fun getPending(): List<MoodEntryEntity> =
        entries.values.filter { it.syncPending }
    override suspend fun getLatest(): MoodEntryEntity? =
        entries.values.maxByOrNull { it.id }
    override suspend fun upsert(entry: MoodEntryEntity) {
        entries[entry.id] = entry
        _flow.value = entries.values.toList()
    }
    override suspend fun upsertAll(entries: List<MoodEntryEntity>) {
        entries.forEach { this.entries[it.id] = it }
        _flow.value = this.entries.values.toList()
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