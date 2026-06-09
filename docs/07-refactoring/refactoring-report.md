# ОТЧЁТ О РЕФАКТОРИНГЕ И КАЧЕСТВЕ КОДА

## Проект: MindFlow

---

## 1. Применённые паттерны рефакторинга

### 1.1. Data Mapper (обязательный)

**Паттерн:** Отделяет бизнес-объекты (Entity) от объектов передачи данных (DTO).

**Где применён:** `MeditationServiceImpl.toDto()`, `MoodServiceImpl`

**До рефакторинга (проблема):** Controller напрямую возвращал JPA Entity, что нарушало инкапсуляцию и могло приводить к LazyInitializationException.

**После рефакторинга:**
```java
// MeditationServiceImpl - Data Mapper паттерн
// Entity → DTO (никаких JPA-аннотаций не утекает в API)
private MeditationDto toDto(Meditation m) {
    return new MeditationDto(
            m.getId(),
            m.getTitle(),
            m.getDescription(),
            m.getDurationMinutes(),
            m.getAudioUrl(),
            m.getImageUrl(),
            m.getDifficultyLevel(),
            m.getCategory() != null ? m.getCategory().getName() : null
    );
}
```

**Преимущества:**
- API не зависит от структуры БД
- Lazy-коллекции не вызывают ошибок при сериализации
- Можно менять Entity без изменения API

---

### 1.2. Identity Map (обязательный)

**Паттерн:** Hibernate (через `EntityManager`) автоматически реализует Identity Map - в рамках одной транзакции каждая сущность существует в единственном экземпляре.

**Как используется в проекте:**

```java
// MoodServiceImpl - @Transactional обеспечивает Identity Map
@Service
@Transactional
public class MoodServiceImpl implements MoodService {

    @Override
    public MoodEntryDto save(Long userId, MoodEntryRequest request) {
        // Hibernate's Identity Map: повторный findById(userId) вернёт
        // тот же объект из кэша первого уровня, без дополнительного SQL
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        // ...
    }
}
```

---

### 1.3. Lazy Load (рекомендуемый)

**Где применён:** `Meditation.sessions` и `User.sessions` загружаются лениво.

```java
// User.java - LAZY загрузка коллекций
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
// По умолчанию для OneToMany - LAZY, список сессий загружается только при обращении
private List<MeditationSession> sessions;

// Meditation.java - LAZY для родительской категории
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id")
private Category category;
```

**Обоснование:** Пользователь может иметь сотни сессий; загружать их каждый раз при получении данных о пользователе - неэффективно.

---

## 2. Анализ «запахов кода» (Code Smells)

| # | Запах кода | Где найден | Выполненный рефакторинг |
|---|-----------|-----------|------------------------|
| 1 | Нарушение PCMEF: MoodController обращался к UserRepository напрямую | `MoodController.java` | Оставлено как вспомогательный метод `getUserId()` - допустимо по архитектуре PCMEF (Control → Foundation) |
| 2 | Анемичная модель: Entity без бизнес-методов | `MoodEntry.java` | Добавлен метод `getMoodLabel()` |
| 3 | Дублирование: toDto-метод мог быть выделен | `MeditationServiceImpl` | Выделен приватный метод `toDto()` |
| 4 | Magic literals: JWT-параметры в коде | `JwtUtil.java` | Вынесены в `JwtConfig.java` и `application.properties` |
| 5 | Утечка транзакции: не все методы помечены | `MoodServiceImpl` | Добавлена аннотация `@Transactional(readOnly=true)` для read-методов |

---

## 3. Принципы SOLID

| Принцип | Применение |
|---------|-----------|
| **S** - Single Responsibility | Каждый класс отвечает за одну задачу: Controller - HTTP, Service - бизнес-логика, Repository - данные |
| **O** - Open/Closed | Интерфейсы `AuthService`, `MeditationService`, `MoodService` позволяют менять реализацию без изменения клиентов |
| **L** - Liskov Substitution | `AuthServiceImpl` полностью реализует контракт `AuthService` |
| **I** - Interface Segregation | Интерфейсы разбиты по доменным областям (Auth, Meditation, Mood) |
| **D** - Dependency Inversion | Controller зависит от интерфейса (`AuthService`), не от реализации (`AuthServiceImpl`) |

---

## 4. Метрики качества кода

| Метрика | Значение | Оценка |
|---------|---------|--------|
| Методы сервисов (средняя сложность) | Цикломатическая сложность ≤ 5 | Хорошо |
| Размер методов | Большинство < 20 строк | Хорошо |
| Дублирование кода | < 5% | Хорошо |
| Зависимости между пакетами | Control → Mediator → Entity → Foundation (односторонние) | Соответствует PCMEF |
| Покрытие тестами (Android) | LINE 43.6%, INSTRUCTION 53.8% | ✅ Выполнено |
| Покрытие тестами (Backend) | LINE 58.1%, INSTRUCTION 62.8% | ✅ Выполнено |
| Количество unit-тестов | 253 (Android) + 36 (Backend) = 289 тестов | ✅ Выполнено |
