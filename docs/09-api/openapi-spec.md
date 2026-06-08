# СПЕЦИФИКАЦИЯ REST API (OpenAPI 3.0)

## Проект: MindFlow Backend

**Базовый URL:** `http://localhost:8081/api`  
**Документация (Swagger UI):** `http://localhost:8081/swagger-ui.html`  
**Версия API:** 1.0.0

---

## Аутентификация

Все эндпоинты (кроме `/auth/**`) требуют заголовок:

```
Authorization: Bearer <access_token>
```

---

## Эндпоинты

### /auth — Аутентификация (публичные)

#### POST /api/auth/register

Регистрация нового пользователя.

**Тело запроса:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "name": "Иван Иванов"
}
```

**Ответ 200 OK:**
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "email": "user@example.com",
  "name": "Иван Иванов",
  "role": "ROLE_USER"
}
```

**Ошибки:** `400 Bad Request` (невалидные данные), `409 Conflict` (email занят)

---

#### POST /api/auth/login

Вход в систему.

**Тело запроса:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

**Ответ 200 OK:** (аналогично register)

**Ошибки:** `401 Unauthorized` (неверный пароль), `404 Not Found` (пользователь не найден)

---

#### POST /api/auth/refresh

Обновление access-токена.

**Заголовок:** `X-Refresh-Token: <refresh_token>`

**Ответ 200 OK:** (новые токены)

**Ошибки:** `401 Unauthorized` (токен истёк или недействителен)

---

### /meditations — Медитации

#### GET /api/meditations

Получение списка активных медитаций с фильтрацией.

**Query параметры:**
| Параметр | Тип | Описание |
|---------|-----|---------|
| `categoryId` | Long | Фильтр по категории |
| `search` | String | Полнотекстовый поиск |

**Ответ 200 OK:**
```json
[
  {
    "id": 1,
    "title": "Базовая медитация осознанности",
    "description": "Простая техника наблюдения за дыханием",
    "durationMinutes": 10,
    "audioUrl": null,
    "imageUrl": "/images/mindfulness.jpg",
    "difficultyLevel": "BEGINNER",
    "categoryName": "Осознанность"
  }
]
```

---

#### GET /api/meditations/{id}

Получение медитации по идентификатору.

**Path параметр:** `id` — идентификатор медитации

**Ответ 200 OK:** MeditationDto

**Ошибки:** `404 Not Found`

---

### /mood — Дневник настроения

#### POST /api/mood

Сохранение записи о настроении.

**Требует:** JWT Bearer Token

**Тело запроса:**
```json
{
  "score": 7,
  "note": "Сегодня был продуктивный день"
}
```

**Ответ 200 OK:**
```json
{
  "id": 42,
  "score": 7,
  "note": "Сегодня был продуктивный день",
  "recordedAt": "2026-06-08T14:30:00",
  "moodLabel": "Хорошо"
}
```

**Ошибки:** `400 Bad Request` (score вне диапазона 1–10)

---

#### GET /api/mood

История записей настроения.

**Требует:** JWT Bearer Token

**Query параметры:** `days` (default=30) — количество дней истории

**Ответ 200 OK:** `List<MoodEntryDto>`

---

#### GET /api/mood/today

Запись настроения за сегодня.

**Требует:** JWT Bearer Token

**Ответ 200 OK:** `MoodEntryDto`  
**Ответ 204 No Content:** если записи нет

---

#### GET /api/mood/average

Средняя оценка настроения за период.

**Требует:** JWT Bearer Token

**Query параметры:** `days` (default=30)

**Ответ 200 OK:**
```json
7.3
```

---

#### DELETE /api/mood/{id}

Удаление записи о настроении.

**Требует:** JWT Bearer Token

**Ответ 204 No Content**

**Ошибки:** `403 Forbidden` (попытка удалить чужую запись), `404 Not Found`

---

## Стандартные HTTP-коды

| Код | Описание |
|-----|---------|
| 200 | OK — успешный запрос |
| 201 | Created — ресурс создан |
| 204 | No Content — успешно, без тела ответа |
| 400 | Bad Request — ошибка валидации |
| 401 | Unauthorized — требуется аутентификация |
| 403 | Forbidden — нет доступа |
| 404 | Not Found — ресурс не найден |
| 409 | Conflict — конфликт (дублирование) |
| 500 | Internal Server Error |

---

## Итог: 12 эндпоинтов (требование ≥ 8 — выполнено)

| # | Метод | Путь |
|---|-------|------|
| 1 | POST | /api/auth/register |
| 2 | POST | /api/auth/login |
| 3 | POST | /api/auth/refresh |
| 4 | GET | /api/meditations |
| 5 | GET | /api/meditations?search= |
| 6 | GET | /api/meditations?categoryId= |
| 7 | GET | /api/meditations/{id} |
| 8 | POST | /api/mood |
| 9 | GET | /api/mood |
| 10 | GET | /api/mood/today |
| 11 | GET | /api/mood/average |
| 12 | DELETE | /api/mood/{id} |
