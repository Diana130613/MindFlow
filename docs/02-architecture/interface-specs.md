# СПЕЦИФИКАЦИЯ ИНТЕРФЕЙСОВ МЕЖДУ СЛОЯМИ

## 1. Android — Интерфейсы слоя Mediator (Repository)

### IMeditationRepository
```kotlin
interface IMeditationRepository {
    suspend fun getAllMeditations(): List<Meditation>
    suspend fun getMeditationById(id: Long): Meditation?
    suspend fun getMeditationsByCategory(categoryId: Long): List<Meditation>
    suspend fun searchMeditations(query: String): List<Meditation>
    suspend fun saveMeditationSession(session: MeditationSession): Long
    suspend fun getUserSessions(userId: Long): List<MeditationSession>
}
```

### IMoodRepository
```kotlin
interface IMoodRepository {
    suspend fun saveMoodEntry(entry: MoodEntry): Long
    suspend fun getMoodEntriesByUser(userId: Long): List<MoodEntry>
    suspend fun getMoodEntriesForPeriod(
        userId: Long,
        from: LocalDate,
        to: LocalDate
    ): List<MoodEntry>
    suspend fun updateMoodEntry(entry: MoodEntry): Boolean
    suspend fun deleteMoodEntry(id: Long): Boolean
    suspend fun getTodayEntry(userId: Long): MoodEntry?
}
```

### IAuthRepository
```kotlin
interface IAuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(
        email: String,
        password: String,
        username: String
    ): AuthResult
    suspend fun logout(): Boolean
    suspend fun refreshToken(): AuthResult
    fun getCurrentUser(): User?
    fun isAuthenticated(): Boolean
    fun saveTokens(accessToken: String, refreshToken: String)
    fun clearTokens()
}
```

## 2. Backend — Интерфейсы слоя Mediator (Service)
### IMeditationService (Java)
```java
public interface IMeditationService {
    List<MeditationDto> getAllMeditations();
    MeditationDto getMeditationById(Long id);
    List<MeditationDto> getMeditationsByCategory(Long categoryId);
    List<MeditationDto> searchMeditations(String query, String type);
    MeditationSessionDto saveSession(
        Long userId, 
        MeditationSessionRequest request
    );
    UserProgressDto getUserProgress(Long userId);
}
```

### IMoodService
```java
public interface IMoodService {
    MoodEntryDto saveMoodEntry(Long userId, MoodEntryRequest request);
    List<MoodEntryDto> getMoodHistory(Long userId, int days);
    MoodAnalyticsDto getMoodAnalytics(
        Long userId, 
        LocalDate from, 
        LocalDate to
    );
    MoodEntryDto updateMoodEntry(Long id, Long userId, MoodEntryRequest request);
    void deleteMoodEntry(Long id, Long userId);
}
```

## IAuthService
```java
public interface IAuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String token);
    UserDto getCurrentUser(Long userId);
}
```

## 3. Backend — Интерфейсы слоя Foundation (Repository)
```java
public interface IMeditationRepository 
    extends JpaRepository<MeditationEntity, Long> {
    
    List<MeditationEntity> findByCategoryId(Long categoryId);
    
    @Query("SELECT m FROM MeditationEntity m WHERE " +
           "LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<MeditationEntity> searchByQuery(@Param("q") String query);
    
    List<MeditationEntity> findByType(MeditationType type);
}
```

## 4. Таблица контрактов
| Интерфейс                         | Слой From         | Слой To                | Транспорт        |
|-----------------------------------|-------------------|------------------------|------------------|
| `IMeditationRepository` (Android) | ViewModel (C)     | RepositoryImpl (M)     | Kotlin call      |
| `IMoodRepository` (Android)       | ViewModel (C)     | RepositoryImpl (M)     | Kotlin coroutine |
| `IAuthRepository` (Android)       | AuthViewModel (C) | AuthRepositoryImpl (M) | Kotlin coroutine |
| `IMeditationService` (Server)     | Controller (C)    | Service (M)            | Java method      |
| `IMoodService` (Server)           | Controller (C)    | Service (M)            | Java method      |
| `IMeditationJpaRepository`        | Service (M)       | JPA (F)                | Spring Data      |