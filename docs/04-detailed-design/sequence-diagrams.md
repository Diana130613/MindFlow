# ДИАГРАММЫ ПОСЛЕДОВАТЕЛЬНОСТИ

## Проект: MindFlow

---

## Сценарий 1: Аутентификация пользователя (UC-02)

```plantuml
@startuml
title UC-02: Вход в систему

actor Пользователь as U
participant "LoginScreen\n[Presentation]" as P
participant "AuthViewModel\n[Control]" as C
participant "AuthRepositoryImpl\n[Mediator]" as M
participant "MindFlowApiService\n[Foundation/Retrofit]" as F
participant "DataStore\n[Foundation]" as DS
participant "AuthController\n[Backend Control]" as BC
participant "AuthServiceImpl\n[Backend Mediator]" as BM
participant "UserRepository\n[Backend Foundation]" as BF
database "PostgreSQL" as DB

U -> P : вводит email + пароль, нажимает "Войти"
P -> C : onLoginClick(email, password)
C -> C : _uiState = Loading
C -> M : login(email, password)
M -> F : POST /api/auth/login {email, password}
F -> BC : HTTP POST /api/auth/login
BC -> BM : login(LoginRequest)
BM -> BF : findByEmail(email)
BF -> DB : SELECT * FROM users WHERE email=?
DB --> BF : UserEntity
BF --> BM : Optional<UserEntity>
BM -> BM : BCrypt.matches(password, hash)
BM -> BM : JwtUtil.generateTokens(user)
BM --> BC : AuthResponse {accessToken, refreshToken}
BC --> F : HTTP 200 OK + AuthResponse
F --> M : AuthResponse
M -> DS : saveTokens(accessToken, refreshToken)
M --> C : AuthResult.Success(user)
C -> C : _uiState = Success
C --> P : navigateTo(HomeScreen)

@enduml
```

---

## Сценарий 2: Запись настроения (UC-05)

```plantuml
@startuml
title UC-05: Запись настроения

actor Пользователь as U
participant "MoodDiaryScreen\n[Presentation]" as P
participant "MoodViewModel\n[Control]" as C
participant "MoodRepositoryImpl\n[Mediator]" as M
participant "MoodEntryDao\n[Foundation/Room]" as DAO
participant "MindFlowApiService\n[Foundation/Retrofit]" as API
participant "MoodController\n[Backend Control]" as BC
participant "MoodServiceImpl\n[Backend Mediator]" as BM
database "Room (SQLite)" as Local
database "PostgreSQL" as Remote

U -> P : выбирает оценку (1-10) и заметку
P -> C : onSaveMood(score=7, note="Хороший день")
C -> M : saveMoodEntry(MoodEntry(score=7, note=...))

note over M: offline-first стратегия
M -> DAO : insert(moodEntryDbEntity{syncPending=true})
DAO -> Local : INSERT INTO mood_entries
Local --> DAO : OK
DAO --> M : id=42

M --> C : Result.Success(id=42)
C -> C : _uiState = Success (оптимистичный UI)
C --> P : показать подтверждение

note over M: фоновая синхронизация
M -> API : POST /api/mood {score, note}
API -> BC : HTTP POST /api/mood + Bearer token
BC -> BM : save(userId, MoodEntryRequest)
BM -> BM : валидация, создание MoodEntry
BM --> BC : MoodEntryDto
BC --> API : HTTP 201 Created
API --> M : MoodEntryDto

alt Синхронизация успешна
  M -> DAO : update(id=42, syncPending=false)
else Нет сети
  note over M: запись остаётся с syncPending=true\nбудет синхронизирована позже
end

@enduml
```

---

## Сценарий 3: Запуск медитации (UC-04)

```plantuml
@startuml
title UC-04: Запуск сессии медитации

actor Пользователь as U
participant "MeditationListScreen\n[Presentation]" as List
participant "MeditationViewModel\n[Control]" as C
participant "MeditationRepositoryImpl\n[Mediator]" as M
participant "MeditationDao\n[Foundation/Room]" as DAO
participant "MindFlowApiService\n[Foundation/Retrofit]" as API
participant "MeditationController\n[Backend Control]" as BC
database "Room (SQLite)" as Local
database "PostgreSQL" as Remote

U -> List : нажимает на медитацию (id=3)
List -> C : onMeditationSelected(id=3)
C -> M : getMeditationById(3)

M -> DAO : findById(3)
DAO -> Local : SELECT * FROM meditations WHERE id=3

alt Кэш есть и актуален
  Local --> DAO : MeditationDbEntity
  DAO --> M : MeditationDbEntity
  M --> C : Meditation(domain)
else Кэш пуст / устарел
  Local --> DAO : null
  M -> API : GET /api/meditations/3
  API -> BC : HTTP GET /api/meditations/3
  BC --> API : MeditationDto
  API --> M : MeditationDto
  M -> DAO : insert(toDbEntity(dto))
  M --> C : Meditation(domain)
end

C -> C : _uiState = MeditationLoaded
C --> List : navigateTo(MeditationDetailScreen)

U -> List : нажимает "Начать"
C -> C : startTimer()
note over C: таймер отсчитывает длительность медитации

U -> List : завершает медитацию
C -> M : saveMeditationSession(userId, meditationId, durationSec)
M -> API : POST /api/meditations (session data)
API -> Remote : сохранение сессии
M --> C : SessionSaved
C --> List : navigateTo(CompletionScreen)

@enduml
```
