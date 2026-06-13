# Руководство администратора — MindFlow

**Версия:** 1.0  
**Дата:** 2026-06-13

---

## 1. Введение

Данное руководство предназначено для системного администратора, отвечающего за развёртывание, настройку и сопровождение серверной части приложения MindFlow.

**Компоненты системы:**
- Backend-сервер: Java Spring Boot (JAR или Docker)
- База данных: PostgreSQL 15
- Android-клиент: APK (распространяется отдельно)

---

## 2. Системные требования

### 2.1 Сервер
| Компонент | Минимум | Рекомендуется |
|-----------|---------|---------------|
| CPU | 1 vCPU | 2 vCPU |
| RAM | 1 ГБ | 2 ГБ |
| Диск | 10 ГБ | 20 ГБ |
| ОС | Ubuntu 20.04 / Windows 10 | Ubuntu 22.04 LTS |
| Java | JDK 17 | JDK 21 |
| PostgreSQL | 14 | 15 |
| Docker | 20.10+ | 24.x |

### 2.2 Сетевые требования
- Открытый порт **8081** (API-сервер)
- Открытый порт **5433** (PostgreSQL, только локально)
- Доступ к интернету для первоначальной загрузки зависимостей Maven

---

## 3. Установка и запуск

### 3.1 Способ 1: Docker Compose (рекомендуется)

```bash
# 1. Клонировать репозиторий
git clone https://github.com/Diana130613/MindFlow.git
cd MindFlow

# 2. Запустить все сервисы
docker-compose up -d

# 3. Проверить статус
docker-compose ps

# 4. Просмотр логов сервера
docker-compose logs -f backend
```

**Порты после запуска:**
- Backend API: http://localhost:8081
- Swagger UI: http://localhost:8081/swagger-ui.html
- PostgreSQL: localhost:5433

**Остановка:**
```bash
docker-compose down
# С удалением данных БД:
docker-compose down -v
```

---

### 3.2 Способ 2: Ручной запуск

#### Шаг 1. Настройка PostgreSQL

```sql
-- Создать базу данных и пользователя
CREATE DATABASE mindflow_db;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE mindflow_db TO postgres;
```

#### Шаг 2. Сборка бэкенда

```bash
cd backend
./mvnw clean package -DskipTests
```

Артефакт: `backend/target/backend-0.0.1-SNAPSHOT.jar`

#### Шаг 3. Запуск с переменными окружения

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/mindflow_db \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
APP_JWT_SECRET=your_very_secret_key_min_256_bits \
SERVER_PORT=8081 \
java -jar backend/target/backend-0.0.1-SNAPSHOT.jar
```

---

## 4. Переменные окружения

| Переменная | Описание | Значение по умолчанию |
|------------|----------|-----------------------|
| `SPRING_DATASOURCE_URL` | URL подключения к PostgreSQL | jdbc:postgresql://localhost:5433/mindflow_db |
| `SPRING_DATASOURCE_USERNAME` | Пользователь БД | postgres |
| `SPRING_DATASOURCE_PASSWORD` | Пароль БД | postgres |
| `APP_JWT_SECRET` | Секрет для подписи JWT (мин. 32 символа) | mindflow_super_secret_key... |
| `APP_JWT_ACCESS_EXPIRATION` | Время жизни access-token (мс) | 900000 (15 мин) |
| `APP_JWT_REFRESH_EXPIRATION` | Время жизни refresh-token (мс) | 604800000 (7 дней) |
| `SERVER_PORT` | Порт HTTP-сервера | 8081 |

> ⚠️ **Обязательно** замените `APP_JWT_SECRET` на случайную строку длиной ≥ 32 символов в продакшн-окружении!

---

## 5. Управление данными

### 5.1 Первоначальная инициализация

При первом запуске Hibernate автоматически создаёт схему (`ddl-auto=update`), затем выполняется `data.sql` с тестовыми категориями и медитациями.

Для ручной инициализации:
```bash
psql -U postgres -d mindflow_db -f docs/03-database/ddl-scripts.sql
psql -U postgres -d mindflow_db -f backend/src/main/resources/data.sql
```

### 5.2 Резервное копирование

```bash
# Создать бэкап
pg_dump -U postgres mindflow_db > backup_$(date +%Y%m%d).sql

# Восстановить из бэкапа
psql -U postgres mindflow_db < backup_20260613.sql
```

### 5.3 Мониторинг состояния

```bash
# Проверить работоспособность API
curl http://localhost:8081/api-docs

# Проверить соединение с БД (из docker-контейнера)
docker exec -it mindflow-db psql -U mindflow_user -d mindflow_db -c "SELECT 1;"
```

---

## 6. Управление пользователями

### 6.1 Роли
| Роль | Права |
|------|-------|
| `ROLE_USER` | Доступ к медитациям, дневнику настроения, профилю |
| `ROLE_ADMIN` | Все права ROLE_USER + управление контентом (API) |

### 6.2 Создание администратора

Напрямую через SQL (после регистрации через API):
```sql
UPDATE users 
SET role = 'ROLE_ADMIN' 
WHERE email = 'admin@mindflow.ru';
```

---

## 7. Логирование

Логи выводятся в `stdout`. При запуске через Docker:
```bash
docker-compose logs -f backend --tail=100
```

Уровни логирования настраиваются в `application.properties`:
```properties
logging.level.ru.mindflow=INFO
logging.level.org.springframework.security=WARN
```

---

## 8. Обновление системы

```bash
# 1. Остановить текущую версию
docker-compose down

# 2. Получить новый код
git pull origin main

# 3. Пересобрать и запустить
docker-compose up -d --build
```

---

## 9. Диагностика типичных проблем

| Симптом | Причина | Решение |
|---------|---------|---------|
| `Connection refused` на порту 8081 | Сервер не запущен | Проверить `docker-compose ps`, просмотреть логи |
| `FATAL: password authentication failed` | Неверные данные БД | Проверить переменные окружения |
| `401 Unauthorized` на всех запросах | Истёк JWT или неверный секрет | Обновить `APP_JWT_SECRET`, перезапустить |
| Hibernate ошибка схемы | Несовместимая версия БД | Убедиться, что PostgreSQL ≥ 14 |
| `Port 8081 already in use` | Порт занят другим процессом | `lsof -i :8081`, завершить процесс |

---

## 10. Контакты и поддержка

**Разработчик:** Хатуаева Дайана Алиевна  
**Группа:** ПИЖ-б-о-23-2, СКФУ  
**Репозиторий:** https://github.com/Diana130613/MindFlow  
**Issues:** https://github.com/Diana130613/MindFlow/issues