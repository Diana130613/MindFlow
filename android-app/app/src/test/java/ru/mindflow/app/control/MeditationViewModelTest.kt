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
import ru.mindflow.app.entity.Meditation
import ru.mindflow.app.mediator.IMeditationRepository

@OptIn(ExperimentalCoroutinesApi::class)
class MeditationViewModelTest {

    private lateinit var repo: FakeMeditationRepo
    private lateinit var viewModel: MeditationViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repo = FakeMeditationRepo()
        viewModel = MeditationViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test fun `initial selected is null`() = assertNull(viewModel.uiState.value.selected)
    @Test fun `initial searchQuery is empty`() = assertEquals("", viewModel.uiState.value.searchQuery)
    @Test fun `initial error is null`() = assertNull(viewModel.uiState.value.error)
    @Test fun `initial isSyncing is false after init sync`() = assertFalse(viewModel.uiState.value.isSyncing)

    // ── MeditationListUiState data class ──────────────────────────────────────

    @Test fun `MeditationListUiState default values`() {
        val state = MeditationListUiState()
        assertTrue(state.meditations.isEmpty())
        assertNull(state.selected)
        assertEquals("", state.searchQuery)
        assertFalse(state.isSyncing)
        assertNull(state.error)
    }

    @Test fun `MeditationListUiState copy works`() {
        val m = Meditation(1L, "Test", null, 10, null, null, "BEGINNER", null)
        val state = MeditationListUiState().copy(selected = m, isSyncing = true)
        assertEquals(m, state.selected)
        assertTrue(state.isSyncing)
        assertNull(state.error)
    }

    // ── sync ──────────────────────────────────────────────────────────────────

    @Test fun `sync on success clears isSyncing`() {
        viewModel.sync()
        assertFalse(viewModel.uiState.value.isSyncing)
    }

    @Test fun `sync on failure sets error message`() {
        repo.shouldFailSync = true
        viewModel.sync()
        assertNotNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isSyncing)
    }

    // ── selectMeditation / clearSelection ────────────────────────────────────

    @Test fun `selectMeditation stores meditation in state`() {
        val m = Meditation(1L, "Дыхание", null, 10, null, null, null, null)
        viewModel.selectMeditation(m)
        assertEquals(m, viewModel.uiState.value.selected)
    }

    @Test fun `clearSelection sets selected to null`() {
        val m = Meditation(1L, "Дыхание", null, 10, null, null, null, null)
        viewModel.selectMeditation(m)
        viewModel.clearSelection()
        assertNull(viewModel.uiState.value.selected)
    }

    @Test fun `selecting different meditation replaces previous`() {
        val m1 = Meditation(1L, "First", null, 5, null, null, null, null)
        val m2 = Meditation(2L, "Second", null, 10, null, null, null, null)
        viewModel.selectMeditation(m1)
        viewModel.selectMeditation(m2)
        assertEquals(m2, viewModel.uiState.value.selected)
    }
}

// ── Fake ─────────────────────────────────────────────────name────────────────

class FakeMeditationRepo : IMeditationRepository {
    var shouldFailSync = false
    private val _all = MutableStateFlow<List<Meditation>>(emptyList())

    override fun getAll(): Flow<List<Meditation>> = _all
    override fun search(query: String): Flow<List<Meditation>> = _all
    override suspend fun getById(id: Long): Meditation? = null
    override suspend fun sync() {
        if (shouldFailSync) throw RuntimeException("sync failed")
    }
}