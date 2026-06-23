# ОТЧЁТ О РЕАЛИЗАЦИИ ЯДРА СИСТЕМЫ

## Проект: MindFlow

---

## 1. Структура проекта (Backend)

```
backend/
├── src/main/java/ru/mindflow/backend/
│   ├── BackendApplication.java          - точка входа Spring Boot
│   ├── config/
│   │   ├── JwtConfig.java               - конфигурация JWT-параметров
│   │   └── SecurityConfig.java          - настройка Spring Security
│   ├── control/                         [C - Control слой]
│   │   ├── AuthController.java          - /api/auth/**
│   │   ├── MeditationController.java    - /api/meditations/**
│   │   └── MoodController.java          - /api/mood/**
│   ├── mediator/                        [M - Mediator слой]
│   │   ├── AuthService.java             - интерфейс
│   │   ├── AuthServiceImpl.java         - реализация
│   │   ├── MeditationService.java       - интерфейс
│   │   ├── MeditationServiceImpl.java   - реализация
│   │   ├── MoodService.java             - интерфейс
│   │   └── MoodServiceImpl.java         - реализация
│   ├── entity/                          [E - Entity слой]
│   │   ├── User.java                    - пользователь + Role enum
│   │   ├── Meditation.java              - медитация
│   │   ├── MoodEntry.java               - запись настроения + getMoodLabel()
│   │   ├── MeditationSession.java       - сессия медитации
│   │   └── Category.java               - категория
│   ├── foundation/                      [F - Foundation слой]
│   │   ├── UserRepository.java
│   │   ├── MeditationRepository.java
│   │   ├── MoodEntryRepository.java
│   │   ├── MeditationSessionRepository.java
│   │   └── CategoryRepository.java
│   ├── security/
│   │   ├── JwtUtil.java                 - генерация и валидация JWT
│   │   ├── JwtAuthFilter.java           - фильтр Spring Security
│   │   └── UserDetailsServiceImpl.java
│   └── dto/
│       ├── LoginRequest.java / RegisterRequest.java
│       ├── AuthResponse.java
│       ├── MeditationDto.java
│       ├── MoodEntryDto.java / MoodEntryRequest.java
└── src/main/resources/
    └── application.properties
```

---

## 2. Реализация слоёв по PCMEF

### 2.1. Entity (E) - бизнес-объекты

Entity-классы содержат **бизнес-методы** (не анемичные модели):

```java
// MoodEntry.java - Entity с бизнес-методом
@Entity
@Table(name = "mood_entries")
public class MoodEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Бизнес-метод Entity-слоя - поведение, а не только данные
    public String getMoodLabel() {
        return switch (score) {
            case 9, 10 -> "Отлично";
            case 7, 8  -> "Хорошо";
            case 5, 6  -> "Нормально";
            case 3, 4  -> "Плохо";
            default    -> "Очень плохо";
        };
    }
}
```

### 2.2. Foundation (F) - доступ к данным

Repository-интерфейсы расширяют `JpaRepository` и содержат только запросы данных:

```java
// MeditationRepository.java - Foundation слой
public interface MeditationRepository extends JpaRepository<Meditation, Long> {
    List<Meditation> findByActiveTrue();
    List<Meditation> findByCategoryIdAndActiveTrue(Long categoryId);

    @Query("SELECT m FROM Meditation m WHERE " +
           "LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Meditation> searchByQuery(@Param("q") String q);
}
```

### 2.3. Mediator (M) - бизнес-логика

Service-классы координируют несколько репозиториев и содержат бизнес-логику:

```java
// MeditationServiceImpl.java - Mediator слой
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeditationServiceImpl implements MeditationService {

    private final MeditationRepository meditationRepository;

    @Override
    public MeditationDto getById(Long id) {
        return meditationRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() ->
                    new IllegalArgumentException("Медитация не найдена: " + id));
    }

    // Data Mapper: Entity → DTO (паттерн Data Mapper)
    private MeditationDto toDto(Meditation m) {
        return new MeditationDto(
                m.getId(), m.getTitle(), m.getDescription(),
                m.getDurationMinutes(), m.getAudioUrl(), m.getImageUrl(),
                m.getDifficultyLevel(),
                m.getCategory() != null ? m.getCategory().getName() : null
        );
    }
}
```

### 2.4. Control (C) - контроллеры REST API

Контроллеры принимают HTTP-запросы и делегируют в Service:

```java
// MoodController.java - Control слой
@RestController
@RequestMapping("/api/mood")
@RequiredArgsConstructor
public class MoodController {

    private final MoodService moodService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<MoodEntryDto> save(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MoodEntryRequest request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(moodService.save(userId, request));
    }
}
```

### 2.5. Безопасность (JWT)

JWT-фильтр перехватывает каждый запрос и извлекает пользователя из токена:

```java
// JwtAuthFilter.java - Spring Security Chain
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(...) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            // Устанавливаем SecurityContext для текущего запроса
        }
        filterChain.doFilter(request, response);
    }
}
```

---

## 3. Соответствие требованиям

| Требование | Целевое значение | Фактическое |
|-----------|-----------------|-------------|
| Количество сущностей | ≥ 5 | 5 (User, Meditation, MoodEntry, MeditationSession, Category) |
| REST API эндпоинтов | ≥ 8 | 12 |
| JWT-аутентификация | Обязательно | ✅ |
| PCMEF архитектура | Обязательно | ✅ (4 пакета: control, mediator, entity, foundation) |
| Тесты (покрытие) | > 40% | ✅ Выполнено |

---

## 4. Запуск и проверка

```bash
# Запуск бэкенда
cd backend
./mvnw spring-boot:run

# Регистрация пользователя
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"Pass123!","name":"Тест"}'

# Получение медитаций (с токеном)
curl http://localhost:8081/api/meditations \
  -H "Authorization: Bearer <access_token>"
```
