package ru.mindflow.app.mediator

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import ru.mindflow.app.foundation.local.dao.GardenDao
import ru.mindflow.app.foundation.local.entity.GardenEntity

class GardenRepositoryImplTest {

    private val dao = FakeGardenDao()
    private val repo = GardenRepositoryImpl(dao)

    @Test fun `get returns default GardenEntity when dao has nothing`() = runBlocking {
        val entity = repo.get()
        assertEquals(1, entity.id)
        assertEquals(1, entity.treeLevel)
        assertEquals(0, entity.flowersCount)
        assertEquals(0L, entity.totalMeditationMinutes)
    }

    @Test fun `get returns stored entity`() = runBlocking {
        dao.save(GardenEntity(treeLevel = 3, totalMeditationMinutes = 120L))
        val entity = repo.get()
        assertEquals(3, entity.treeLevel)
        assertEquals(120L, entity.totalMeditationMinutes)
    }

    @Test fun `save persists entity in dao`() = runBlocking {
        val entity = GardenEntity(treeLevel = 5, currentStreakDays = 7)
        repo.save(entity)
        assertEquals(5, dao.storedEntity?.treeLevel)
        assertEquals(7, dao.storedEntity?.currentStreakDays)
    }

    @Test fun `observe reflects current dao value`() = runBlocking {
        dao.save(GardenEntity(treeLevel = 4))
        val observed = repo.observe().first()
        assertEquals(4, observed?.treeLevel)
    }

    @Test fun `observe initially emits null`() = runBlocking {
        val fresh = GardenRepositoryImpl(FakeGardenDao())
        assertNull(fresh.observe().first())
    }

    @Test fun `save then get returns same entity`() = runBlocking {
        val entity = GardenEntity(treeLevel = 6, flowersCount = 5, currentStreakDays = 10)
        repo.save(entity)
        val retrieved = repo.get()
        assertEquals(entity.treeLevel, retrieved.treeLevel)
        assertEquals(entity.flowersCount, retrieved.flowersCount)
        assertEquals(entity.currentStreakDays, retrieved.currentStreakDays)
    }
}

class FakeGardenDao : GardenDao {
    var storedEntity: GardenEntity? = null
    private val _flow = MutableStateFlow<GardenEntity?>(null)

    override fun observe(): Flow<GardenEntity?> = _flow
    override suspend fun get(): GardenEntity? = storedEntity
    override suspend fun save(entity: GardenEntity) {
        storedEntity = entity
        _flow.value = entity
    }
}