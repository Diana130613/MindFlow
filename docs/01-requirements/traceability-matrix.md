# ТАБЛИЦА ТРАССИРОВКИ ТРЕБОВАНИЙ

## Проект: MindFlow

### Трассировка: Бизнес-прецеденты → Системные Use Case → Компоненты

| BUC (Бизнес) | UC (Системный) | Приоритет | Реализующий компонент | Статус |
|---|---|---|---|---|
| BUC-01 Практика медитации | UC-03 Просмотр списка медитаций | Высокий | MeditationController, MeditationServiceImpl, MeditationRepository | Реализовано |
| BUC-01 Практика медитации | UC-04 Запуск сессии медитации | Высокий | MeditationController, MeditationSessionRepository | Реализовано |
| BUC-01 Практика медитации | UC-08 Работа офлайн | Высокий | Room DB (Android), MeditationDao | В разработке |
| BUC-02 Управление состоянием | UC-05 Запись настроения | Высокий | MoodController, MoodServiceImpl, MoodEntryRepository | Реализовано |
| BUC-02 Управление состоянием | UC-06 Аналитика настроения | Средний | MoodController (/average), MoodServiceImpl | Реализовано |
| BUC-03 Формирование привычки | UC-12 Просмотр прогресса | Средний | UserProgress (Entity) | В разработке |
| BUC-04 Дыхательные техники | UC-11 Дыхательное упражнение | Средний | BreathingScreen (Android) | В разработке |
| BUC-05 Управление контентом | UC-09 Управление контентом | Средний | MeditationController (Admin), CategoryRepository | В разработке |
| BUC-06 Мониторинг | UC-10 Управление пользователями | Средний | UserRepository, AuthServiceImpl | Реализовано |
| BUC-07 Персонализация | UC-07 Управление профилем | Средний | UserRepository, AuthController | Реализовано |
| — | UC-01 Регистрация | Высокий | AuthController, AuthServiceImpl | Реализовано |
| — | UC-02 Вход в систему (JWT) | Высокий | AuthController, JwtUtil, SecurityConfig | Реализовано |

---

### Трассировка: Use Case → Эндпоинты API

| UC | HTTP-метод | Эндпоинт | Статус |
|----|-----------|---------|--------|
| UC-01 Регистрация | POST | /api/auth/register | ✅ |
| UC-02 Вход | POST | /api/auth/login | ✅ |
| UC-02 Обновление токена | POST | /api/auth/refresh | ✅ |
| UC-03 Список медитаций | GET | /api/meditations | ✅ |
| UC-03 Поиск медитаций | GET | /api/meditations?search= | ✅ |
| UC-03 По категории | GET | /api/meditations?categoryId= | ✅ |
| UC-04 Детали медитации | GET | /api/meditations/{id} | ✅ |
| UC-05 Сохранить настроение | POST | /api/mood | ✅ |
| UC-05 Настроение сегодня | GET | /api/mood/today | ✅ |
| UC-06 История настроения | GET | /api/mood?days=30 | ✅ |
| UC-06 Средняя оценка | GET | /api/mood/average | ✅ |
| UC-05 Удалить запись | DELETE | /api/mood/{id} | ✅ |

**Итого эндпоинтов: 12 (требование ≥ 8 — выполнено)**

---

### Трассировка: Use Case → Сущности БД

| UC | Задействованные таблицы |
|----|------------------------|
| UC-01 Регистрация | users |
| UC-02 Вход | users |
| UC-03 Просмотр медитаций | meditations, categories |
| UC-04 Запуск сессии | meditation_sessions, meditations, users |
| UC-05 Запись настроения | mood_entries, users |
| UC-06 Аналитика | mood_entries |
| UC-07 Профиль | users |
| UC-09 Управление контентом | meditations, categories |
| UC-10 Управление пользователями | users |
| UC-12 Прогресс | meditation_sessions, users |
