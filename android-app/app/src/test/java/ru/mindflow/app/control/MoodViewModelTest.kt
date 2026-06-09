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
import ru.mindflow.app.entity.MoodEntry
import ru.mindflow.app.mediator.IMoodRepository

@OptIn(ExperimentalCoroutinesApi::class)
class MoodViewModelTest {

    private lateinit var repo: FakeMoodRepo
    private lateinit var viewModel: MoodViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repo = FakeMoodRepo()
        viewModel = MoodViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test fun `initial isSaving is false`() = assertFalse(viewModel.uiState.value.isSaving)
    @Test fun `initial savedEntry is null`() = assertNull(viewModel.uiState.value.savedEntry)
    @Test fun `initial error is null`() = assertNull(viewModel.uiState.value.error)
    @Test fun `initial average is 0`() = assertEquals(0.0, viewModel.uiState.value.average, 0.0)

    // ── MoodUiState data class ────────────────────────────────────────────────

    @Test fun `MoodUiState default values`() {
        val state = MoodUiState()
        assertFalse(state.isSaving)
        assertNull(state.savedEntry)
        assertNull(state.error)
        assertEquals(0.0, state.average, 0.0)
    }

    @Test fun `MoodUiState copy changes fields`() {
        val entry = MoodEntry(1L, 7, null, "Хорошо", "2026-01-01")
        val state = MoodUiState().copy(isSaving = false, savedEntry = entry)
        assertEquals(entry, state.savedEntry)
        assertFalse(state.isSaving)
    }

    // ── saveMood ──────────────────────────────────────────────────────────────

    @Test fun `saveMood on success stores entry`() {
        viewModel.saveMood(7, null)
        assertNotNull(viewModel.uiState.value.savedEntry)
        assertEquals(7, viewModel.uiState.value.savedEntry?.score)
    }

    @Test fun `saveMood on success clears isSaving`() {
        viewModel.saveMood(7, null)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test fun `saveMood with note stores note`() {
        viewModel.saveMood(9, "great day")
        assertEquals("great day", viewModel.uiState.value.savedEntry?.note)
    }

    @Test fun `saveMood on failure sets error`() {
        repo.shouldFailSave = true
        viewModel.saveMood(5, null)
        assertNotNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test fun `saveMood on failure clears isSaving`() {
        repo.shouldFailSave = true
        viewModel.saveMood(5, null)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    // ── resetSaved ────────────────────────────────────────────────────────────

    @Test fun `resetSaved clears savedEntry`() {
        viewModel.saveMood(7, null)
        viewModel.resetSaved()
        assertNull(viewModel.uiState.value.savedEntry)
    }

    // ── deleteMood ────────────────────────────────────────────────────────────

    @Test fun `deleteMood calls repo delete`() {
        viewModel.deleteMood(42L)
        assertEquals(42L, repo.deletedId)
    }

    // ── average loading ───────────────────────────────────────────────────────

    @Test fun `average is loaded on init`() {
        repo.averageValue = 7.5
        val vm = MoodViewModel(repo)
        assertEquals(7.5, vm.uiState.value.average, 0.01)
    }
}

// ── Fake ─────────────────────────────────────────────────────────────────────

class FakeMoodRepo : IMoodRepository {
    var shouldFailSave = false
    var deletedId: Long? = null
    var averageValue: Double = 0.0
    private val _history = MutableStateFlow<List<MoodEntry>>(emptyList())

    override fun getHistory(): Flow<List<MoodEntry>> = _history
    override suspend fun getTodayEntry(): MoodEntry? = null
    override suspend fun save(score: Int, note: String?): MoodEntry {
        if (shouldFailSave) throw RuntimeException("save failed")
        return MoodEntry(System.currentTimeMillis(), score, note, "label", "2026-01-01")
    }
    override suspend fun delete(id: Long) { deletedId = id }
    override suspend fun getAverage(days: Int): Double = averageValue
    override suspend fun syncPending() {}
}