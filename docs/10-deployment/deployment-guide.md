# РУКОВОДСТВО ПО РАЗВЁРТЫВАНИЮ

## Проект: MindFlow

---

## 1. Требования к окружению

| Компонент | Версия | Назначение |
|-----------|--------|-----------|
| Java JDK | 17+ | Запуск Spring Boot |
| PostgreSQL | 15+ | Основная база данных |
| Maven | 3.8+ | Сборка проекта |
| Docker | 20+ | Контейнеризация (опционально) |
| docker-compose | 2.0+ | Оркестрация (опционально) |
| Android Studio | Hedgehog+ | Сборка Android APK |

---

## 2. Запуск через Docker (рекомендуется)

### 2.1. docker-compose.yml

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: mindflow-db
    environment:
      POSTGRES_DB: mindflow_db
      POSTGRES_USER: mindflow_user
      POSTGRES_PASSWORD: mindflow_pass
    ports:
      - "5433:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./docs/03-database/ddl-scripts.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U mindflow_user -d mindflow_db"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: mindflow-backend
    ports:
      - "8081:8081"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/mindflow_db
      SPRING_DATASOURCE_USERNAME: mindflow_user
      SPRING_DATASOURCE_PASSWORD: mindflow_pass
      APP_JWT_SECRET: mindflow_super_secret_key_change_in_production
    depends_on:
      postgres:
        condition: service_healthy

volumes:
  postgres_data:
```

### 2.2. Dockerfile (backend)

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2.3. Команды запуска

```bash
# Сборка и запуск всего стека
cd course_project
docker-compose up -d

# Проверка работоспособности
curl http://localhost:8081/api/meditations

# Просмотр логов
docker-compose logs -f backend

# Остановка
docker-compose down
```

---

## 3. Ручной запуск (без Docker)

### 3.1. Создание базы данных PostgreSQL

```bash
# Подключиться к PostgreSQL
psql -U postgres

# Создать БД и пользователя
CREATE DATABASE mindflow_db;
CREATE USER mindflow_user WITH PASSWORD 'mindflow_pass';
GRANT ALL PRIVILEGES ON DATABASE mindflow_db TO mindflow_user;
\q

# Применить DDL-скрипты
psql -U mindflow_user -d mindflow_db -f docs/03-database/ddl-scripts.sql
```

### 3.2. Настройка application.properties

```properties
# backend/src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://localhost:5433/mindflow_db
spring.datasource.username=mindflow_user
spring.datasource.password=mindflow_pass
spring.jpa.hibernate.ddl-auto=update
app.jwt.secret=YOUR_SECRET_KEY_MIN_256_BITS
server.port=8081
```

### 3.3. Сборка и запуск Spring Boot

```bash
cd backend

# Сборка (без тестов для быстрого запуска)
./mvnw clean package -DskipTests

# Запуск
java -jar target/backend-*.jar

# Или через Maven
./mvnw spring-boot:run
```

Бэкенд доступен по адресу: `http://localhost:8081`  
Swagger UI: `http://localhost:8081/swagger-ui.html`

---

## 4. Сборка Android APK

### 4.1. Через Android Studio

1. Открыть папку `android-app/` в Android Studio
2. Build → Generate Signed Bundle / APK
3. Выбрать APK, указать keystore
4. APK: `android-app/app/build/outputs/apk/release/app-release.apk`

### 4.2. Через командную строку

```bash
cd android-app

# Debug APK
./gradlew assembleDebug

# APK находится по пути:
# app/build/outputs/apk/debug/app-debug.apk
```

### 4.3. Настройка URL бэкенда на Android

В файле `android-app/app/src/main/java/.../network/ApiConfig.kt`:

```kotlin
object ApiConfig {
    // Для эмулятора: 10.0.2.2 - адрес хоста
    // Для реального устройства: IP-адрес компьютера в сети
    const val BASE_URL = "http://10.0.2.2:8081/api/"
}
```

---

## 5. Проверка развёртывания

```bash
# Health check
curl http://localhost:8081/actuator/health

# Тестовая регистрация
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@mindflow.ru","password":"Admin123!","name":"Admin"}'

# Получение медитаций (без авторизации вернёт 401)
curl http://localhost:8081/api/meditations
```

---

## 6. Переменные окружения

| Переменная | По умолчанию | Описание |
|-----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | jdbc:postgresql://localhost:5433/mindflow_db | URL подключения к БД |
| `SPRING_DATASOURCE_USERNAME` | postgres | Пользователь БД |
| `SPRING_DATASOURCE_PASSWORD` | - | Пароль БД |
| `APP_JWT_SECRET` | - | Секретный ключ JWT (min 256 бит) |
| `SERVER_PORT` | 8081 | Порт сервера |
