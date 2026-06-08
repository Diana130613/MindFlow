# ПЛАН И ОТЧЁТ О ТЕСТИРОВАНИИ

## Проект: MindFlow

---

## 1. Стратегия тестирования

| Тип тестирования | Инструмент | Охват | Цель |
|-----------------|-----------|-------|------|
| Модульное (Unit) | JUnit 5, Mockito | Mediator (Service) | Изолированная проверка бизнес-логики |
| Интеграционное | Spring Boot Test | Control + Mediator + Foundation | Проверка взаимодействия слоёв |
| API-тестирование | MockMvc | Control (Controllers) | Проверка HTTP-эндпоинтов |
| Покрытие кода | JaCoCo | Весь проект | Целевое покрытие > 40% |

---

## 2. Модульные тесты (Unit Tests)

### Тест 1: MoodEntry.getMoodLabel()

```java
// Тест бизнес-метода Entity-слоя
@Test
void getMoodLabel_returnsCorrectLabel_forDifferentScores() {
    MoodEntry entry = new MoodEntry();

    entry.setScore(10); assertEquals("Отлично", entry.getMoodLabel());
    entry.setScore(8);  assertEquals("Хорошо",  entry.getMoodLabel());
    entry.setScore(6);  assertEquals("Нормально", entry.getMoodLabel());
    entry.setScore(4);  assertEquals("Плохо",   entry.getMoodLabel());
    entry.setScore(1);  assertEquals("Очень плохо", entry.getMoodLabel());
}
```

### Тест 2: MoodServiceImpl — сохранение записи

```java
@ExtendWith(MockitoExtension.class)
class MoodServiceImplTest {

    @Mock private MoodEntryRepository moodRepo;
    @Mock private UserRepository userRepo;
    @InjectMocks private MoodServiceImpl moodService;

    @Test
    void save_createsMoodEntry_withCorrectScore() {
        // Arrange
        User user = User.builder().id(1L).email("a@b.com").build();
        MoodEntryRequest req = new MoodEntryRequest(7, "Хороший день");
        MoodEntry saved = MoodEntry.builder()
                .id(1L).score(7).note("Хороший день").user(user).build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(moodRepo.save(any())).thenReturn(saved);

        // Act
        MoodEntryDto result = moodService.save(1L, req);

        // Assert
        assertNotNull(result);
        assertEquals(7, result.getScore());
        assertEquals("Хороший день", result.getNote());
        verify(moodRepo, times(1)).save(any());
    }

    @Test
    void save_throwsException_whenUserNotFound() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> moodService.save(99L, new MoodEntryRequest(5, null)));
    }
}
```

### Тест 3: MeditationServiceImpl — поиск

```java
@Test
void getById_throwsException_whenMeditationNotFound() {
    when(meditationRepo.findById(999L)).thenReturn(Optional.empty());
    assertThrows(IllegalArgumentException.class,
            () -> meditationService.getById(999L));
}

@Test
void getAll_returnsOnlyActiveMeditations() {
    List<Meditation> active = List.of(
            Meditation.builder().id(1L).title("Test").active(true).build()
    );
    when(meditationRepo.findByActiveTrue()).thenReturn(active);

    List<MeditationDto> result = meditationService.getAll();
    assertEquals(1, result.size());
    verify(meditationRepo).findByActiveTrue();
}
```

---

## 3. Интеграционные тесты (API)

```java
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class MoodControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test@test.com", roles = "USER")
    void postMood_returns200_withValidRequest() throws Exception {
        MoodEntryRequest req = new MoodEntryRequest(8, "Отличное настроение");

        mockMvc.perform(post("/api/mood")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(8));
    }

    @Test
    void postMood_returns401_withoutAuth() throws Exception {
        mockMvc.perform(post("/api/mood")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"score\":5}"))
                .andExpect(status().isUnauthorized());
    }
}
```

---

## 4. Тест-кейсы

| ID | Тест-кейс | Входные данные | Ожидаемый результат | Статус |
|----|----------|---------------|--------------------|----|
| TC-01 | Регистрация с корректными данными | email=a@b.com, pass=Pass123! | HTTP 200, токены в ответе | ✅ |
| TC-02 | Регистрация с существующим email | email=существующий | HTTP 400 / 409 | ✅ |
| TC-03 | Вход с правильными данными | email+password | HTTP 200, JWT токены | ✅ |
| TC-04 | Вход с неверным паролем | неверный пароль | HTTP 401 | ✅ |
| TC-05 | Получение медитаций без токена | — | HTTP 401 Unauthorized | ✅ |
| TC-06 | Получение медитаций с токеном | Bearer <token> | HTTP 200, список | ✅ |
| TC-07 | Сохранение настроения score=7 | score=7, note="Test" | HTTP 200, MoodEntryDto | ✅ |
| TC-08 | Сохранение настроения score=0 | score=0 | HTTP 400 (валидация) | ✅ |
| TC-09 | Сохранение настроения score=11 | score=11 | HTTP 400 (валидация) | ✅ |
| TC-10 | getMoodLabel() при score=10 | score=10 | "Отлично" | ✅ |
| TC-11 | Поиск медитации по запросу | search=сон | Список медитаций по теме сна | ✅ |
| TC-12 | Удаление записи настроения | id=существующий | HTTP 204 No Content | ✅ |

---

## 5. Конфигурация тестовой БД

```properties
# application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
app.jwt.secret=test-secret-key-for-testing-purposes-only
```
