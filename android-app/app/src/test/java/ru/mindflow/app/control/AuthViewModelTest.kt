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

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var repo: FakeAuthRepoForAuth
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repo = FakeAuthRepoForAuth()
        viewModel = AuthViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── AuthUiState sealed class ──────────────────────────────────────────────

    @Test fun `initial state is Idle`() = assertTrue(viewModel.uiState.value is AuthUiState.Idle)

    @Test fun `AuthUiState Error stores message`() {
        val err = AuthUiState.Error("Something went wrong")
        assertEquals("Something went wrong", err.message)
    }

    @Test fun `AuthUiState Error equality`() {
        assertEquals(AuthUiState.Error("msg"), AuthUiState.Error("msg"))
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test fun `login success sets Success state`() {
        repo.loginResult = Result.success(User("a@test.com", "Alice"))
        viewModel.login("a@test.com", "pass")
        assertTrue(viewModel.uiState.value is AuthUiState.Success)
    }

    @Test fun `login failure sets Error state`() {
        repo.loginResult = Result.failure(RuntimeException("Invalid credentials"))
        viewModel.login("a@test.com", "wrong")
        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Error)
        assertEquals("Invalid credentials", (state as AuthUiState.Error).message)
    }

    @Test fun `login trims email whitespace`() {
        repo.loginResult = Result.success(User("a@test.com", "Alice"))
        viewModel.login("  a@test.com  ", "pass")
        assertEquals("a@test.com", repo.lastLoginEmail)
    }

    @Test fun `login failure without message uses default error text`() {
        repo.loginResult = Result.failure(RuntimeException())
        viewModel.login("a@test.com", "pass")
        val state = viewModel.uiState.value as AuthUiState.Error
        assertEquals("Ошибка входа", state.message)
    }

    // ── register ──────────────────────────────────────────────────────────────

    @Test fun `register success sets Success state`() {
        repo.registerResult = Result.success(User("new@test.com", "Bob"))
        viewModel.register("new@test.com", "pass", "Bob")
        assertTrue(viewModel.uiState.value is AuthUiState.Success)
    }

    @Test fun `register failure sets Error state`() {
        repo.registerResult = Result.failure(RuntimeException("Email taken"))
        viewModel.register("exists@test.com", "pass", "Alice")
        assertTrue(viewModel.uiState.value is AuthUiState.Error)
        assertEquals("Email taken", (viewModel.uiState.value as AuthUiState.Error).message)
    }

    @Test fun `register trims email and name`() {
        repo.registerResult = Result.success(User("b@test.com", "Bob"))
        viewModel.register("  b@test.com  ", "pass", "  Bob  ")
        assertEquals("b@test.com", repo.lastRegisterEmail)
        assertEquals("Bob", repo.lastRegisterName)
    }

    // ── resetState ────────────────────────────────────────────────────────────

    @Test fun `resetState returns to Idle`() {
        repo.loginResult = Result.success(User("a@test.com", "Alice"))
        viewModel.login("a@test.com", "pass")
        viewModel.resetState()
        assertTrue(viewModel.uiState.value is AuthUiState.Idle)
    }
}

// ── Fake ─────────────────────────────────────────────────────────────────────

class FakeAuthRepoForAuth : ru.mindflow.app.mediator.IAuthRepository {
    var loginResult: Result<User> = Result.failure(UnsupportedOperationException())
    var registerResult: Result<User> = Result.failure(UnsupportedOperationException())
    var lastLoginEmail: String? = null
    var lastRegisterEmail: String? = null
    var lastRegisterName: String? = null

    override suspend fun login(email: String, password: String): Result<User> {
        lastLoginEmail = email
        return loginResult
    }
    override suspend fun register(email: String, password: String, name: String): Result<User> {
        lastRegisterEmail = email
        lastRegisterName = name
        return registerResult
    }
    override suspend fun logout() {}
    override suspend fun isLoggedIn() = false
    override suspend fun getCurrentUser(): User? = null
    override suspend fun updateName(name: String) {}
    override suspend fun getJoinDateMillis(): Long? = null
}