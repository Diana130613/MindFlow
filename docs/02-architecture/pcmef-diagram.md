# АРХИТЕКТУРА PCMEF — MindFlow

## Адаптация PCMEF для мобильной платформы

| Слой PCMEF | Реализация на Android | Реализация на Backend |
|-----------|----------------------|----------------------|
| **P — Presentation** | Composable-функции, Activity | — |
| **C — Control** | ViewModel + StateFlow | REST Controllers |
| **M — Mediator** | Repository (domain layer) | Service классы |
| **E — Entity** | Data классы (domain) | JPA Entity классы |
| **F — Foundation** | Room DAO + Retrofit API | JPA Repository |

## PlantUML — Диаграмма пакетов

```plantuml
@startuml
skinparam packageStyle rectangle
skinparam rectangle {
  BorderColor #666666
  FontSize 12
}
title Архитектура PCMEF — MindFlow

package "Android Client" {

  package "presentation (P)" #LavenderBlush {
    rectangle "HomeScreen" as P1
    rectangle "MeditationListScreen" as P2
    rectangle "MoodDiaryScreen" as P3
    rectangle "ProfileScreen" as P4
    rectangle "BreathingScreen" as P5
  }

  package "control (C — ViewModel)" #LightCyan {
    rectangle "HomeViewModel" as C1
    rectangle "MeditationViewModel" as C2
    rectangle "MoodViewModel" as C3
    rectangle "AuthViewModel" as C4
    rectangle "BreathingViewModel" as C5
  }

  package "mediator (M — Repository)" #LightGreen {
    interface "IMeditationRepository" as IM1
    interface "IMoodRepository" as IM2
    interface "IAuthRepository" as IM3
    rectangle "MeditationRepositoryImpl" as M1
    rectangle "MoodRepositoryImpl" as M2
    rectangle "AuthRepositoryImpl" as M3
  }

  package "entity (E — Domain)" #LightYellow {
    rectangle "Meditation" as E1
    rectangle "MoodEntry" as E2
    rectangle "User" as E3
    rectangle "MeditationSession" as E4
    rectangle "UserProgress" as E5
  }

  package "foundation (F — Data Sources)" #LightSalmon {
    rectangle "MeditationDao (Room)" as F1
    rectangle "MoodEntryDao (Room)" as F2
    rectangle "UserDao (Room)" as F3
    rectangle "MindFlowApiService (Retrofit)" as F4
    rectangle "DataStore (Prefs)" as F5
  }
}

package "Backend (Spring Boot)" {

  package "control (C — Controllers)" #LightCyan {
    rectangle "AuthController" as BC1
    rectangle "MeditationController" as BC2
    rectangle "MoodController" as BC3
    rectangle "UserController" as BC4
  }

  package "mediator (M — Services)" #LightGreen {
    interface "IAuthService" as BM1
    interface "IMeditationService" as BM2
    interface "IMoodService" as BM3
    rectangle "AuthServiceImpl" as BS1
    rectangle "MeditationServiceImpl" as BS2
    rectangle "MoodServiceImpl" as BS3
  }

  package "entity (E — JPA)" #LightYellow {
    rectangle "UserEntity" as BE1
    rectangle "MeditationEntity" as BE2
    rectangle "MoodEntryEntity" as BE3
    rectangle "MeditationSessionEntity" as BE4
    rectangle "UserProgressEntity" as BE5
    rectangle "CategoryEntity" as BE6
  }

  package "foundation (F — Repositories)" #LightSalmon {
    rectangle "UserRepository" as BF1
    rectangle "MeditationRepository" as BF2
    rectangle "MoodEntryRepository" as BF3
    rectangle "MeditationSessionRepository" as BF4
    rectangle "UserProgressRepository" as BF5
  }
}

database "PostgreSQL" as DB #AliceBlue
database "Room (SQLite)" as RoomDB #AliceBlue

' ── Зависимости Android ────────────────────────────────────────────

P1 --> C1 : использует
P2 --> C2 : использует
P3 --> C3 : использует
P4 --> C4 : использует
P5 --> C5 : использует

C1 --> IM1 : вызывает
C1 --> IM2 : вызывает
C2 --> IM1 : вызывает
C3 --> IM2 : вызывает
C4 --> IM3 : вызывает
C5 --> IM1 : вызывает

IM1 <|.. M1 : реализует
IM2 <|.. M2 : реализует
IM3 <|.. M3 : реализует

M1 --> E1 : оперирует
M1 --> E4 : оперирует
M2 --> E2 : оперирует
M3 --> E3 : оперирует

M1 --> F1 : локальные данные
M1 --> F4 : сетевые данные
M2 --> F2 : локальные данные
M2 --> F4 : сетевые данные
M3 --> F4 : аутентификация
M3 --> F5 : токены

F1 --> RoomDB
F2 --> RoomDB
F3 --> RoomDB

' ── Зависимости Backend ────────────────────────────────────────────

BC1 --> BM1 : делегирует
BC2 --> BM2 : делегирует
BC3 --> BM3 : делегирует

BM1 <|.. BS1 : реализует
BM2 <|.. BS2 : реализует
BM3 <|.. BS3 : реализует

BS1 --> BE1 : оперирует
BS2 --> BE2 : оперирует
BS2 --> BE4 : оперирует
BS3 --> BE3 : оперирует

BS1 --> BF1 : доступ к данным
BS2 --> BF2 : доступ к данным
BS2 --> BF4 : доступ к данным
BS3 --> BF3 : доступ к данным

BF1 --> DB
BF2 --> DB
BF3 --> DB
BF4 --> DB
BF5 --> DB

' ── Взаимодействие Client ↔ Backend ───────────────────────────────

F4 --> BC1 : POST /api/auth
F4 --> BC2 : GET/POST /api/meditations
F4 --> BC3 : GET/POST /api/mood

@enduml
```

## Описание слоёв
*P — Presentation (Представление)*  
Отвечает только за отображение UI.

- Получает данные через UiState из ViewModel
- Не содержит бизнес-логики
- Не обращается напрямую к данным

```
HomeScreen
    ↓ collectAsState()
HomeViewModel.uiState: StateFlow<HomeUiState>
```

*C — Control (Управление)*  
ViewModel на Android / Controller на Backend.
- Принимает пользовательские события
- Вызывает методы Mediator-слоя
- Преобразует данные в UiState

```
fun onMeditationSelected(id: Long) {
    viewModelScope.launch {
        val meditation = meditationRepository.getById(id)  // → Mediator
        _uiState.value = UiState.Success(meditation)
    }
}
```

*M — Mediator (Посредник)*  
Repository на Android / Service на Backend.

- Координирует несколько источников данных
- Содержит бизнес-логику
- Скрывает детали реализации от Control

```
// Android
class MeditationRepositoryImpl : IMeditationRepository {
    override suspend fun getById(id: Long): Meditation {
        val local = dao.findById(id)      // Foundation (Room)
        if (local != null) return local.toDomain()
        return api.getMeditation(id)      // Foundation (Retrofit)
            .toDomain()
            .also { dao.insert(it.toEntity()) }
    }
}
```

*E — Entity (Сущность)*  
Доменные объекты — ядро бизнес-логики.

- Не зависят от фреймворков
- Содержат бизнес-методы
- Неизменяемы (data class на Android, JPA Entity на Backend)

```  
// Android domain entity
    data class MoodEntry(
    val id: Long,
    val score: Int,
    val note: String?,
    val recordedAt: LocalDateTime
) {
    fun isPositive() = score >= 7
    fun getMoodLabel() = when {
        score >= 9 -> "Отлично"
        score >= 7 -> "Хорошо"
        score >= 5 -> "Нормально"
        score >= 3 -> "Плохо"
        else       -> "Очень плохо"
    }
}  
```

*F — Foundation (Фундамент)*  
Доступ к данным — Room DAO, Retrofit API, DataStore.

- Только CRUD-операции
- Без бизнес-логики
- Скрыт за интерфейсами Mediator-слоя

``` kotlin
@Dao
interface MoodEntryDao {
    @Query("SELECT * FROM mood_entries WHERE user_id = :userId
        ORDER BY recorded_at DESC")
    fun getByUser(userId: Long): Flow<List<MoodEntryDbEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MoodEntryDbEntity)
}
```

## Правила зависимостей PCMEF

| ✅ РАЗРЕШЕНО: | ❌ ЗАПРЕЩЕНО: |
| :--- | :--- |
| P → C | P → M (минуя C) |
| C → M | P → F (минуя C, M) |
| M → E | C → F (минуя M) |
| M → F | F → M (обратная зависимость) |
| E → (ничего) | E → F (Entity не знает о БД) |
| F → (только БД/сеть) | F → C (обратная зависимость) |

## Поток данных (data flow)

Пользователь нажимает кнопку
             ↓
[P] Screen.onEvent()
             ↓
[C] ViewModel.handleEvent()
             ↓
[M] Repository.getData()
           ↙           ↘
[F] Room.query()   [F] API.request()
       ↓                   ↓
локальный кэш      сервер Backend
                           ↓
                    [C] Controller
                           ↓
                    [M] Service
                           ↓
                    [F] JpaRepository
                           ↓
                    PostgreSQL

