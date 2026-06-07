# ДОМЕННАЯ МОДЕЛЬ (Domain Model)

## PlantUML-диаграмма

```plantuml
@startuml
skinparam classBackgroundColor LavenderBlush
skinparam classBorderColor DarkViolet

class User {
  - id: Long
  - email: String
  - passwordHash: String
  - username: String
  - role: Role
  - createdAt: LocalDateTime
  + isAdmin(): Boolean
  + updateProfile(): void
}

class MeditationSession {
  - id: Long
  - startedAt: LocalDateTime
  - completedAt: LocalDateTime
  - durationSeconds: Int
  - completed: Boolean
  - syncPending: Boolean
  + getDurationMinutes(): Int
  + markCompleted(): void
  + markForSync(): void
}

class Meditation {
  - id: Long
  - title: String
  - description: String
  - durationMinutes: Int
  - type: MeditationType
  - difficulty: Difficulty
  - content: String
  + isForBeginners(): Boolean
  + getShortDescription(): String
}

class MoodEntry {
  - id: Long
  - score: Int
  - note: String
  - recordedAt: LocalDateTime
  - syncPending: Boolean
  + isPositive(): Boolean
  + validate(): Boolean
}

class BreathingExercise {
  - id: Long
  - name: String
  - inhaleSeconds: Int
  - holdSeconds: Int
  - exhaleSeconds: Int
  - cycles: Int
  + getTotalDuration(): Int
  + getPattern(): String
}

class UserProgress {
  - id: Long
  - totalSessions: Int
  - totalMinutes: Int
  - currentStreak: Int
  - longestStreak: Int
  - lastSessionDate: LocalDate
  + updateStreak(): void
  + addSession(minutes: Int): void
}

class Category {
  - id: Long
  - name: String
  - description: String
  - iconName: String
}

enum MeditationType {
  GUIDED
  BREATHING
  BODY_SCAN
  VISUALIZATION
  MINDFULNESS
}

enum Difficulty {
  BEGINNER
  INTERMEDIATE
  ADVANCED
}

enum Role {
  ROLE_USER
  ROLE_ADMIN
}

User "1" --> "many" MeditationSession : проводит
User "1" --> "many" MoodEntry : записывает
User "1" --> "1" UserProgress : имеет
MeditationSession "many" --> "1" Meditation : относится к
Meditation "many" --> "1" Category : принадлежит
Meditation --> MeditationType
Meditation --> Difficulty
User --> Role

@enduml
```

## Описание сущностей

| Сущность | Ответственность | Ключевые атрибуты |
| :--- | :--- | :--- |
| User | Хранит данные пользователя, управляет аутентификацией | id, email, passwordHash, role |
| Meditation | Описывает медитативную практику | title, type, duration, content |
| MeditationSession | Фиксирует факт проведения медитации | startedAt, duration, completed |
| MoodEntry | Запись о настроении пользователя | score (1-10), note, tags |
| BreathingExercise | Параметры дыхательного упражнения | inhale, hold, exhale, cycles |
| UserProgress | Агрегированная статистика пользователя | totalSessions, streak |
| Category | Тематические категории медитаций | name, iconName |

## Бизнес-правила
1. Пользователь не может иметь более 1 записи настроения в сутки
2. Серия (streak) прерывается, если пропущен хотя бы 1 день
3. Медитация считается завершённой при прохождении >80% времени
4. Администратор может управлять медитациями, обычный пользователь — только читать