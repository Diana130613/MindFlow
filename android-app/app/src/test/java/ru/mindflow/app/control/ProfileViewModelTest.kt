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
import ru.mindflow.app.entity.User
import ru.mindflow.app.mediator.IAuthRepository

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private lateinit var repo: FakeAuthRepo
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repo = FakeAuthRepo()
        viewModel = ProfileViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Initial load ──────────────────────────────────────────────────────────

    @Test fun `loads user from repository on init`() {
        repo.currentUser = User("alice@test.com", "Alice")
        val vm = ProfileViewModel(repo)
        assertEquals("Alice", vm.uiState.value.user?.name)
        assertEquals("alice@test.com", vm.uiState.value.user?.email)
    }

    @Test fun `user is null when repository returns null`() {
        repo.currentUser = null
        val vm = ProfileViewModel(repo)
        assertNull(vm.uiState.value.user)
    }

    @Test fun `isLoading is false after load completes`() {
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test fun `loggedOut is false initially`() {
        assertFalse(viewModel.uiState.value.loggedOut)
    }

    // ── ProfileUiState data class ─────────────────────────────────────────────

    @Test fun `ProfileUiState default values`() {
        val state = ProfileUiState()
        assertNull(state.user)
        assertFalse(state.isLoading)
        assertFalse(state.loggedOut)
        assertEquals(0L, state.daysWithUs)
    }

    @Test fun `ProfileUiState copy changes fields`() {
        val user = User("a@b.com", "Bob")
        val state = ProfileUiState().copy(user = user, isLoading = false, daysWithUs = 42L)
        assertEquals("Bob", state.user?.name)
        assertEquals(42L, state.daysWithUs)
    }

    // ── daysWithUs calculation ────────────────────────────────────────────────

    @Test fun `daysWithUs is 0 when joinDate is null`() {
        repo.joinDateMillis = null
        val vm = ProfileViewModel(repo)
        assertEquals(0L, vm.uiState.value.daysWithUs)
    }

    @Test fun `daysWithUs is positive when joinDate is in the past`() {
        val threeDaysAgo = System.currentTimeMillis() - (3L * 24 * 60 * 60 * 1000)
        repo.joinDateMillis = threeDaysAgo
        val vm = ProfileViewModel(repo)
        assertTrue(vm.uiState.value.daysWithUs >= 2L)
    }

    // ── updateName ────────────────────────────────────────────────────────────

    @Test fun `updateName updates user name in state`() {
        repo.currentUser = User("alice@test.com", "Alice")
        viewModel = ProfileViewModel(repo)
        viewModel.updateName("NewName")
        assertEquals("NewName", viewModel.uiState.value.user?.name)
    }

    @Test fun `updateName calls repository`() {
        repo.currentUser = User("alice@test.com", "Alice")
        viewModel = ProfileViewModel(repo)
        viewModel.updateName("UpdatedName")
        assertEquals("UpdatedName", repo.updatedName)
    }

    // ── logout ────────────────────────────────────────────────────────────────

    @Test fun `logout sets loggedOut to true`() {
        viewModel.logout()
        assertTrue(viewModel.uiState.value.loggedOut)
    }

    @Test fun `logout calls repository logout`() {
        viewModel.logout()
        assertTrue(repo.logoutCalled)
    }
}

// ── Fake ─────────────────────────────────────────────────────────────────────

class FakeAuthRepo : IAuthRepository {
    var currentUser: User? = null
    var joinDateMillis: Long? = null
    var logoutCalled = false
    var updatedName: String? = null

    override suspend fun login(email: String, password: String) =
        Result.failure<User>(UnsupportedOperationException())
    override suspend fun register(email: String, password: String, name: String) =
        Result.failure<User>(UnsupportedOperationException())
    override suspend fun logout() { logoutCalled = true }
    override suspend fun isLoggedIn() = currentUser != null
    override suspend fun getCurrentUser(): User? = currentUser
    override suspend fun updateName(name: String) { updatedName = name }
    override suspend fun getJoinDateMillis(): Long? = joinDateMillis
}