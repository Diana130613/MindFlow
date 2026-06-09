package ru.mindflow.app.control

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import ru.mindflow.app.foundation.local.entity.GardenEntity
import ru.mindflow.app.mediator.IGardenRepository
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class GardenViewModelTest {

    private lateinit var repo: FakeGardenRepo
    private lateinit var viewModel: GardenViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repo = FakeGardenRepo()
        viewModel = GardenViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test fun `initial state reflects default garden entity`() {
        val s = viewModel.uiState.value
        assertEquals(1, s.level)
        assertEquals(0, s.flowers)
        assertEquals(0L, s.totalMinutes)
        assertEquals(0, s.streakDays)
        assertFalse(s.hasGoldenFlower)
        assertFalse(s.hasRainbowFlower)
        assertTrue(s.unlockedDecorations.isEmpty())
    }

    // ── toUiState — decorations parsing ──────────────────────────────────────

    @Test fun `uiState updates when repo flow emits new entity`() {
        repo.setEntity(GardenEntity(treeLevel = 5, flowersCount = 10))
        assertEquals(5, viewModel.uiState.value.level)
        assertEquals(10, viewModel.uiState.value.flowers)
    }

    @Test fun `toUiState parses blank decorations to empty set`() {
        repo.setEntity(GardenEntity(unlockedDecorations = ""))
        assertTrue(viewModel.uiState.value.unlockedDecorations.isEmpty())
    }

    @Test fun `toUiState parses comma-separated decorations to set`() {
        repo.setEntity(GardenEntity(unlockedDecorations = "stones,bench"))
        assertEquals(setOf("stones", "bench"), viewModel.uiState.value.unlockedDecorations)
    }

    @Test fun `toUiState parses single decoration`() {
        repo.setEntity(GardenEntity(unlockedDecorations = "stones"))
        assertEquals(setOf("stones"), viewModel.uiState.value.unlockedDecorations)
    }

    @Test fun `toUiState hasGoldenFlower true at exactly 7 days`() {
        repo.setEntity(GardenEntity(currentStreakDays = 7))
        assertTrue(viewModel.uiState.value.hasGoldenFlower)
    }

    @Test fun `toUiState hasGoldenFlower false below 7 days`() {
        repo.setEntity(GardenEntity(currentStreakDays = 6))
        assertFalse(viewModel.uiState.value.hasGoldenFlower)
    }

    @Test fun `toUiState hasRainbowFlower true at 30 days`() {
        repo.setEntity(GardenEntity(currentStreakDays = 30))
        assertTrue(viewModel.uiState.value.hasRainbowFlower)
    }

    @Test fun `toUiState hasRainbowFlower false below 30 days`() {
        repo.setEntity(GardenEntity(currentStreakDays = 29))
        assertFalse(viewModel.uiState.value.hasRainbowFlower)
    }

    @Test fun `toUiState level name matches GardenProgress`() {
        repo.setEntity(GardenEntity(treeLevel = 1))
        assertTrue(viewModel.uiState.value.levelName.contains("Росток"))
    }

    // ── recordMeditation — guard ──────────────────────────────────────────────

    @Test fun `recordMeditation ignores zero minutes`() {
        viewModel.recordMeditation(0L)
        assertTrue(repo.savedEntities.isEmpty())
    }

    @Test fun `recordMeditation ignores negative minutes`() {
        viewModel.recordMeditation(-5L)
        assertTrue(repo.savedEntities.isEmpty())
    }

    // ── recordMeditation — minutes accumulation ───────────────────────────────

    @Test fun `recordMeditation stores correct total minutes`() {
        viewModel.recordMeditation(30L)
        assertEquals(30L, repo.savedEntities.last().totalMeditationMinutes)
    }

    @Test fun `recordMeditation accumulates on repeated calls`() {
        viewModel.recordMeditation(30L)
        viewModel.recordMeditation(30L)
        assertEquals(60L, repo.savedEntities.last().totalMeditationMinutes)
    }

    @Test fun `recordMeditation updates tree level at 60 minutes`() {
        viewModel.recordMeditation(60L)
        assertEquals(2, repo.savedEntities.last().treeLevel)
    }

    @Test fun `recordMeditation unlocks first flower at 30 minutes`() {
        viewModel.recordMeditation(30L)
        assertEquals(1, repo.savedEntities.last().flowersCount)
    }

    @Test fun `recordMeditation records today as last practice date`() {
        val today = LocalDate.now().toString()
        viewModel.recordMeditation(10L)
        assertEquals(today, repo.savedEntities.last().lastPracticeDate)
    }

    // ── recordMeditation — streak logic ───────────────────────────────────────

    @Test fun `recordMeditation sets streak to 1 for very first practice`() {
        viewModel.recordMeditation(10L)
        assertEquals(1, repo.savedEntities.last().currentStreakDays)
    }

    @Test fun `recordMeditation increments streak when practiced yesterday`() {
        val yesterday = LocalDate.now().minusDays(1).toString()
        repo.setEntity(GardenEntity(currentStreakDays = 3, lastPracticeDate = yesterday))
        viewModel.recordMeditation(10L)
        assertEquals(4, repo.savedEntities.last().currentStreakDays)
    }

    @Test fun `recordMeditation keeps streak when practiced same day`() {
        val today = LocalDate.now().toString()
        repo.setEntity(GardenEntity(currentStreakDays = 3, lastPracticeDate = today))
        viewModel.recordMeditation(10L)
        assertEquals(3, repo.savedEntities.last().currentStreakDays)
    }

    @Test fun `recordMeditation resets streak to 1 after a gap`() {
        repo.setEntity(GardenEntity(currentStreakDays = 5, lastPracticeDate = "2020-01-01"))
        viewModel.recordMeditation(10L)
        assertEquals(1, repo.savedEntities.last().currentStreakDays)
    }

    @Test fun `recordMeditation unlocks stones decoration at 3-day streak`() {
        val yesterday = LocalDate.now().minusDays(1).toString()
        repo.setEntity(GardenEntity(currentStreakDays = 2, lastPracticeDate = yesterday))
        viewModel.recordMeditation(1L)
        val saved = repo.savedEntities.last()
        assertEquals(3, saved.currentStreakDays)
        assertTrue(saved.unlockedDecorations.contains("stones"))
    }
}

// ── Fake ─────────────────────────────────────────────────────────────────────

class FakeGardenRepo(initial: GardenEntity = GardenEntity()) : IGardenRepository {
    private val _flow = MutableStateFlow<GardenEntity?>(initial)
    private var _current = initial
    val savedEntities = mutableListOf<GardenEntity>()

    fun setEntity(entity: GardenEntity) {
        _current = entity
        _flow.value = entity
    }

    override fun observe(): Flow<GardenEntity?> = _flow
    override suspend fun get(): GardenEntity = _current
    override suspend fun save(entity: GardenEntity) {
        _current = entity
        savedEntities.add(entity)
        _flow.value = entity
    }
}
