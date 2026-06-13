package ru.mindflow.app.mediator

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import ru.mindflow.app.foundation.local.ITokenStorage
import ru.mindflow.app.foundation.remote.api.MindFlowApi
import ru.mindflow.app.foundation.remote.dto.*

class AuthRepositoryImplTest {

    private lateinit var api: FakeApiForAuth
    private lateinit var storage: FakeTokenStorage
    private lateinit var repo: AuthRepositoryImpl

    @Before
    fun setUp() {
        api = FakeApiForAuth()
        storage = FakeTokenStorage()
        repo = AuthRepositoryImpl(api, storage)
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test fun `login success returns User with correct fields`() = runBlocking {
        api.loginResponse = AuthResponse("acc", "ref", "alice@test.com", "Alice", "ROLE_USER")
        val result = repo.login("alice@test.com", "pass")
        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals("alice@test.com", user.email)
        assertEquals("Alice", user.name)
        assertEquals("ROLE_USER", user.role)
    }

    @Test fun `login success saves tokens to storage`() = runBlocking {
        api.loginResponse = AuthResponse("acc123", "ref456", "alice@test.com", "Alice", "ROLE_USER")
        repo.login("alice@test.com", "pass")
        assertEquals("acc123", storage.savedAccessToken)
        assertEquals("ref456", storage.savedRefreshToken)
    }

    @Test fun `login success saves email and name to storage`() = runBlocking {
        api.loginResponse = AuthResponse("a", "r", "bob@test.com", "Bob", "ROLE_USER")
        repo.login("bob@test.com", "pass")
        assertEquals("bob@test.com", storage.savedEmail)
        assertEquals("Bob", storage.savedName)
    }

    @Test fun `login failure returns failure Result`() = runBlocking {
        api.shouldFailLogin = true
        val result = repo.login("bad@test.com", "wrong")
        assertTrue(result.isFailure)
    }

    @Test fun `login failure does not save tokens`() = runBlocking {
        api.shouldFailLogin = true
        repo.login("bad@test.com", "wrong")
        assertNull(storage.savedAccessToken)
    }

    @Test fun `login sends correct email and password to api`() = runBlocking {
        api.loginResponse = AuthResponse("a", "r", "x@test.com", "X", "ROLE_USER")
        repo.login("x@test.com", "mypassword")
        assertEquals("x@test.com", api.lastLoginRequest?.email)
        assertEquals("mypassword", api.lastLoginRequest?.password)
    }

    // ── register ──────────────────────────────────────────────────────────────

    @Test fun `register success returns User with correct fields`() = runBlocking {
        api.registerResponse = AuthResponse("acc", "ref", "new@test.com", "New", "ROLE_USER")
        val result = repo.register("new@test.com", "pass", "New")
        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals("new@test.com", user.email)
        assertEquals("New", user.name)
    }

    @Test fun `register success saves tokens to storage`() = runBlocking {
        api.registerResponse = AuthResponse("tok", "rtok", "new@test.com", "New", "ROLE_USER")
        repo.register("new@test.com", "pass", "New")
        assertEquals("tok", storage.savedAccessToken)
        assertEquals("rtok", storage.savedRefreshToken)
    }

    @Test fun `register failure returns failure Result`() = runBlocking {
        api.shouldFailRegister = true
        val result = repo.register("exists@test.com", "pass", "Alice")
        assertTrue(result.isFailure)
    }

    @Test fun `register failure does not save tokens`() = runBlocking {
        api.shouldFailRegister = true
        repo.register("exists@test.com", "pass", "Alice")
        assertNull(storage.savedAccessToken)
    }

    @Test fun `register sends correct fields to api`() = runBlocking {
        api.registerResponse = AuthResponse("a", "r", "c@test.com", "Charlie", "ROLE_USER")
        repo.register("c@test.com", "pw", "Charlie")
        assertEquals("c@test.com", api.lastRegisterRequest?.email)
        assertEquals("pw", api.lastRegisterRequest?.password)
        assertEquals("Charlie", api.lastRegisterRequest?.name)
    }

    // ── logout ────────────────────────────────────────────────────────────────

    @Test fun `logout calls storage clear`() = runBlocking {
        repo.logout()
        assertTrue(storage.cleared)
    }

    // ── isLoggedIn ────────────────────────────────────────────────────────────

    @Test fun `isLoggedIn returns true when storage has token`() = runBlocking {
        storage.loggedIn = true
        assertTrue(repo.isLoggedIn())
    }

    @Test fun `isLoggedIn returns false when storage has no token`() = runBlocking {
        storage.loggedIn = false
        assertFalse(repo.isLoggedIn())
    }

    // ── getCurrentUser ────────────────────────────────────────────────────────

    @Test fun `getCurrentUser returns User when email and name present`() = runBlocking {
        storage.email = "alice@test.com"
        storage.name = "Alice"
        val user = repo.getCurrentUser()
        assertNotNull(user)
        assertEquals("alice@test.com", user?.email)
        assertEquals("Alice", user?.name)
    }

    @Test fun `getCurrentUser returns null when email is missing`() = runBlocking {
        storage.email = null
        storage.name = "Alice"
        assertNull(repo.getCurrentUser())
    }

    @Test fun `getCurrentUser returns null when name is missing`() = runBlocking {
        storage.email = "alice@test.com"
        storage.name = null
        assertNull(repo.getCurrentUser())
    }

    // ── updateName ────────────────────────────────────────────────────────────

    @Test fun `updateName delegates to storage`() = runBlocking {
        repo.updateName("NewName")
        assertEquals("NewName", storage.updatedName)
    }

    // ── getJoinDateMillis ─────────────────────────────────────────────────────

    @Test fun `getJoinDateMillis returns value from storage`() = runBlocking {
        storage.joinDateMillis = 123456789L
        assertEquals(123456789L, repo.getJoinDateMillis())
    }

    @Test fun `getJoinDateMillis returns null when storage has none`() = runBlocking {
        storage.joinDateMillis = null
        assertNull(repo.getJoinDateMillis())
    }
}

// ── Fakes ─────────────────────────────────────────────────────────────────────

class FakeApiForAuth : MindFlowApi {
    var loginResponse: AuthResponse = AuthResponse("", "", "", "", "")
    var registerResponse: AuthResponse = AuthResponse("", "", "", "", "")
    var shouldFailLogin = false
    var shouldFailRegister = false
    var lastLoginRequest: LoginRequest? = null
    var lastRegisterRequest: RegisterRequest? = null

    override suspend fun login(request: LoginRequest): AuthResponse {
        lastLoginRequest = request
        if (shouldFailLogin) throw RuntimeException("Invalid credentials")
        return loginResponse
    }

    override suspend fun register(request: RegisterRequest): AuthResponse {
        lastRegisterRequest = request
        if (shouldFailRegister) throw RuntimeException("Email already taken")
        return registerResponse
    }

    override suspend fun refresh(token: String): AuthResponse = throw UnsupportedOperationException()
    override suspend fun getMeditations(categoryId: Long?, search: String?) = emptyList<ru.mindflow.app.foundation.remote.dto.MeditationDto>()
    override suspend fun getMeditationById(id: Long): ru.mindflow.app.foundation.remote.dto.MeditationDto = throw UnsupportedOperationException()
    override suspend fun saveMood(request: MoodEntryRequest): MoodEntryDto = throw UnsupportedOperationException()
    override suspend fun getMoodHistory(days: Int) = emptyList<MoodEntryDto>()
    override suspend fun getMoodToday(): MoodEntryDto? = null
    override suspend fun getMoodAverage(days: Int) = 0.0
    override suspend fun deleteMood(id: Long) {}
}

class FakeTokenStorage : ITokenStorage {
    var savedAccessToken: String? = null
    var savedRefreshToken: String? = null
    var savedEmail: String? = null
    var savedName: String? = null
    var cleared = false
    var loggedIn = false
    var email: String? = null
    var name: String? = null
    var joinDateMillis: Long? = null
    var updatedName: String? = null

    override suspend fun saveTokens(accessToken: String, refreshToken: String, email: String, name: String) {
        savedAccessToken = accessToken
        savedRefreshToken = refreshToken
        savedEmail = email
        savedName = name
        this.email = email
        this.name = name
        loggedIn = true
    }

    override suspend fun updateName(name: String) { updatedName = name; this.name = name }
    override suspend fun getAccessToken(): String? = if (loggedIn) savedAccessToken else null
    override suspend fun getRefreshToken(): String? = if (loggedIn) savedRefreshToken else null
    override suspend fun getUserEmail(): String? = email
    override suspend fun getUserName(): String? = name
    override suspend fun getJoinDateMillis(): Long? = joinDateMillis
    override suspend fun isLoggedIn(): Boolean = loggedIn
    override suspend fun clear() { cleared = true; loggedIn = false; email = null; name = null }
}