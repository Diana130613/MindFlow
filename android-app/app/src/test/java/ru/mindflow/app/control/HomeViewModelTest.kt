package ru.mindflow.app.control

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import ru.mindflow.app.entity.MoodEntry
import ru.mindflow.app.entity.User

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var authRepo: FakeAuthRepo2
    private lateinit var moodRepo: FakeMoodRepo2
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        authRepo = FakeAuthRepo2()
        moodRepo = FakeMoodRepo2()
        viewModel = HomeViewModel(authRepo, moodRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── HomeUiState data class ────────────────────────────────────────────────

    @Test fun `HomeUiState default user is null`() = assertNull(HomeUiState().user)
    @Test fun `HomeUiState default todayMood is null`() = assertNull(HomeUiState().todayMood)
    @Test fun `HomeUiState default weeklyAverage is 0`() = assertEquals(0.0, HomeUiState().weeklyAverage, 0.0)
    @Test fun `HomeUiState default isLoading is false`() = assertFalse(HomeUiState().isLoading)

    @Test fun `HomeUiState copy preserves unchanged fields`() {
        val user = User("a@b.com", "Alice")
        val state = HomeUiState().copy(user = user, weeklyAverage = 7.5)
        assertEquals(user, state.user)
        assertEquals(7.5, state.weeklyAverage, 0.01)
        assertFalse(state.isLoading)
    }

    // ── Initial load ──────────────────────────────────────────────────────────

    @Test fun `loads user from auth repository on init`() {
        authRepo.user = User("alice@test.com", "Alice")
        val vm = HomeViewModel(authRepo, moodRepo)
        assertEquals("Alice", vm.uiState.value.user?.name)
    }

    @Test fun `user is null when not logged in`() {
        authRepo.user = null
        val vm = HomeViewModel(authRepo, moodRepo)
        assertNull(vm.uiState.value.user)
    }

    @Test fun `loads today mood from repo`() {
        val entry = MoodEntry(1L, 8, null, "Хорошо", "2026-06-09")
        moodRepo.todayEntry = entry
        val vm = HomeViewModel(authRepo, moodRepo)
        assertEquals(8, vm.uiState.value.todayMood?.score)
    }

    @Test fun `todayMood is null when repo returns null`() {
        moodRepo.todayEntry = null
        val vm = HomeViewModel(authRepo, moodRepo)
        assertNull(vm.uiState.value.todayMood)
    }

    @Test fun `isLoading is false after init completes`() {
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test fun `weeklyAverage loaded from repo`() {
        moodRepo.average = 6.5
        val vm = HomeViewModel(authRepo, moodRepo)
        assertEquals(6.5, vm.uiState.value.weeklyAverage, 0.01)
    }

    // ── load() ────────────────────────────────────────────────────────────────

    @Test fun `calling load again refreshes user`() {
        authRepo.user = User("alice@test.com", "Alice")
        viewModel = HomeViewModel(authRepo, moodRepo)
        authRepo.user = User("bob@test.com", "Bob")
        viewModel.load()
        assertEquals("Bob", viewModel.uiState.value.user?.name)
    }

    @Test fun `weeklyAverage defaults to 0 when repo throws`() {
        moodRepo.shouldFailAverage = true
        val vm = HomeViewModel(authRepo, moodRepo)
        assertEquals(0.0, vm.uiState.value.weeklyAverage, 0.0)
    }
}

// ── Fakes ─────────────────────────────────────────────────────────────────────

class FakeAuthRepo2 : ru.mindflow.app.mediator.IAuthRepository {
    var user: User? = null
    override suspend fun login(email: String, password: String) = Result.failure<User>(UnsupportedOperationException())
    override suspend fun register(email: String, password: String, name: String) = Result.failure<User>(UnsupportedOperationException())
    override suspend fun logout() {}
    override suspend fun isLoggedIn() = user != null
    override suspend fun getCurrentUser(): User? = user
    override suspend fun updateName(name: String) {}
    override suspend fun getJoinDateMillis(): Long? = null
}

class FakeMoodRepo2 : ru.mindflow.app.mediator.IMoodRepository {
    var todayEntry: MoodEntry? = null
    var average: Double = 0.0
    var shouldFailAverage = false
    private val _history = kotlinx.coroutines.flow.MutableStateFlow<List<MoodEntry>>(emptyList())

    override fun getHistory(): kotlinx.coroutines.flow.Flow<List<MoodEntry>> = _history
    override suspend fun getTodayEntry(): MoodEntry? = todayEntry
    override suspend fun save(score: Int, note: String?): MoodEntry =
        MoodEntry(1L, score, note, "label", "2026-01-01")
    override suspend fun delete(id: Long) {}
    override suspend fun getAverage(days: Int): Double {
        if (shouldFailAverage) throw RuntimeException("no connection")
        return average
    }
    override suspend fun syncPending() {}
}