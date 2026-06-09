# MindFlow — Мобильное приложение для ментального здоровья и медитации

**Автор:** Хатуаева Дайана  
**Группа:** ПИЖ-б-о-23-2  
**Траектория:** В — Мобильная разработка  
**Дата начала:** 01.05.2026  
**Дата сдачи:** 30.05.2026

---

## Описание проекта

MindFlow — мобильное приложение для Android, предназначенное для поддержки ментального здоровья пользователей. Система предоставляет библиотеку guided-медитаций, упражнения по дыхательным практикам и дневник настроения с визуализацией динамики эмоционального состояния. Приложение работает в офлайн-режиме с автоматической синхронизацией данных при восстановлении соединения.

---

## Траектория выполнения

- [ ] Десктопная (JavaFX/Swing)
- [ ] Веб-ориентированная (Spring MVC / React)
- [x] **Мобильная (Android Kotlin + Spring Boot)** ← *выбрана*
- [ ] Enterprise (Full Stack)

---

## Технологический стек

| Компонент | Технология |
|-----------|-----------|
| Android-клиент | Kotlin, Jetpack Compose, Material Design 3 |
| Архитектура клиента | MVVM + PCMEF, StateFlow, ViewModel |
| Сеть (Android) | Retrofit 2, OkHttp |
| Локальное хранилище | Room (SQLite) |
| DI | Hilt |
| Бэкенд | Java 17, Spring Boot 3.2 |
| База данных | PostgreSQL 15 |
| ORM | Spring Data JPA, Hibernate |
| Безопасность | Spring Security, JWT |
| API-документация | Swagger / OpenAPI 3.0 |
| Сборка | Maven (бэкенд), Gradle (Android) |
| Контейнеризация | Docker, docker-compose |

---

## Требования к окружению

| Требование | Версия |
|------------|--------|
| Java JDK | 17+ |
| Android Studio | Hedgehog+ |
| PostgreSQL | 15+ |
| Maven | 3.8+ |
| Docker | 20+ (опционально) |
| Android SDK | API 21+ |

---

## Установка и запуск

### 1. Клонирование репозитория

```bash
git clone https://github.com/dkhatuaeva/mindflow.git
cd mindflow
```

### 2. Запуск бэкенда вручную

```bash
cd backend

# Создать БД
psql -U postgres -c "CREATE DATABASE mindflow_db;"

# Запустить Spring Boot
./mvnw spring-boot:run
```

Бэкенд запустится на `http://localhost:8081`  
Swagger UI: `http://localhost:8081/swagger-ui.html`

### 3. Запуск через Docker

```bash
docker-compose up -d
```

### 4. Сборка Android APK

Открыть `android-app/` в Android Studio → Build → Generate Signed APK, или:

```bash
cd android-app
./gradlew assembleDebug
# APK: android-app/app/build/outputs/apk/debug/app-debug.apk
```

---

## API Endpoints

Базовый URL: `http://localhost:8081/api`

| Метод | Эндпоинт | Описание | Доступ |
|-------|---------|---------|--------|
| POST | /auth/register | Регистрация | Публичный |
| POST | /auth/login | Вход в систему | Публичный |
| POST | /auth/refresh | Обновление токена | Публичный |
| GET | /meditations | Список медитаций | USER, ADMIN |
| GET | /meditations/{id} | Детали медитации | USER, ADMIN |
| GET | /meditations?categoryId=1 | По категории | USER, ADMIN |
| GET | /meditations?search=текст | Поиск | USER, ADMIN |
| POST | /mood | Сохранить запись настроения | USER |
| GET | /mood | История настроения | USER |
| GET | /mood/today | Запись за сегодня | USER |
| GET | /mood/average | Средняя оценка | USER |
| DELETE | /mood/{id} | Удалить запись | USER |

Полная документация API: [Swagger UI](http://localhost:8081/swagger-ui.html)

---

## Структура документации

Вся документация в папке [`docs/`](docs/):

| Папка | Содержимое |
|-------|-----------|
| [00-project-charter/](docs/00-project-charter/) | Паспорт проекта, BUC, SWOT, глоссарий, стейкхолдеры |
| [01-requirements/](docs/01-requirements/) | Use Case, Domain Model, спецификации прецедентов |
| [02-architecture/](docs/02-architecture/) | PCMEF, Arc42, ADR, интерфейсы |
| [03-database/](docs/03-database/) | ER-диаграмма, DDL-скрипты, стратегия ORM |
| [04-detailed-design/](docs/04-detailed-design/) | Sequence-диаграммы, диаграммы классов |
| [05-implementation/](docs/05-implementation/) | Отчёт о реализации слоёв |
| [06-testing/](docs/06-testing/) | Тест-план, JaCoCo, результаты |
| [07-refactoring/](docs/07-refactoring/) | Анализ кода, паттерны, отчёт |
| [08-ui/](docs/08-ui/) | Скриншоты интерфейса, UX-описание |
| [09-api/](docs/09-api/) | OpenAPI спецификация, Postman-коллекция |
| [10-deployment/](docs/10-deployment/) | Docker, инструкция по развёртыванию |
| [11-user-guide/](docs/11-user-guide/) | Руководство пользователя |
| [12-final-report/](docs/12-final-report/) | Пояснительная записка |

---

## Архитектура (PCMEF)

Система построена на паттерне PCMEF (Presentation-Control-Mediator-Entity-Foundation).

| Слой | Android | Backend | Ответственность |
|------|---------|---------|----------------|
| P — Presentation | Composable-функции | — | UI, отображение |
| C — Control | ViewModel | REST Controllers | Обработка событий |
| M — Mediator | Repository | Service | Бизнес-логика |
| E — Entity | Data-классы | JPA Entity | Бизнес-объекты |
| F — Foundation | Room DAO + Retrofit | JPA Repository | Доступ к данным |

Подробнее: [docs/02-architecture/arc42-overview.md](docs/02-architecture/arc42-overview.md)

---

## Статистика разработки

### Метрики Git

| Метрика | Значение |
|---------|---------|
| Всего коммитов | 61 |
| Период разработки | 01.05.2026 – 09.06.2026 |
| Ветки | main |
| Тестов (Android) | 253 |
| Тестов (Backend) | 36 |
| Покрытие Android (LINE) | 43.6 % |
| Покрытие Backend (LINE) | 58.1 % |

### График активности

> Скриншоты статистики сделаны из GitHub Insights на момент сдачи проекта.

![Активность коммитов](docs/02-architecture/image.png)

---

## Авторы

- **Хатуаева Дайана** — разработчик, документация  
  Группа ПИЖ-б-о-23-2

---

## Лицензия

MIT License — подробности в файле [LICENSE](LICENSE).
