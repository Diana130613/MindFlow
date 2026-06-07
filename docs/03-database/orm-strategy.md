# СТРАТЕГИЯ ORM — Маппинг Entity → Таблицы

## Инструмент: Spring Data JPA + Hibernate

## Таблица маппинга

| Java Entity             | Таблица БД          | Стратегия загрузки   |
|-------------------------|---------------------|----------------------|
| UserEntity              | users               | EAGER (базовые поля) |
| MeditationEntity        | meditations         | LAZY (content)       |
| MeditationSessionEntity | meditation_sessions | LAZY                 |
| MoodEntryEntity         | mood_entries        | EAGER                |
| UserProgressEntity      | user_progress       | EAGER                |
| CategoryEntity          | categories          | EAGER                |
| BreathingExerciseEntity | breathing_exercises | EAGER                |

## Стратегии связей

| Связь                 | Тип        | FetchType  | CascadeType  |
|-----------------------|------------|------------|--------------|
| User → Sessions       | @OneToMany | LAZY       | ALL          |
| User → MoodEntries    | @OneToMany | LAZY       | ALL          |
| User → Progress       | @OneToOne  | EAGER      | ALL          |
| Meditation → Category | @ManyToOne | EAGER      | NONE         |
| Session → Meditation  | @ManyToOne | LAZY       | NONE         |

## Обоснование LAZY для Sessions
- Пользователь может иметь сотни сессий
- Большинство запросов не требуют полного списка
- Lazy Loading предотвращает N+1 через @EntityGraph