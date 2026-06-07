-- ============================================
-- MindFlow Database Schema
-- PostgreSQL 15+
-- ============================================

-- Удаление таблиц (для пересоздания)
DROP TABLE IF EXISTS meditation_sessions CASCADE;
DROP TABLE IF EXISTS mood_entries CASCADE;
DROP TABLE IF EXISTS user_progress CASCADE;
DROP TABLE IF EXISTS meditations CASCADE;
DROP TABLE IF EXISTS breathing_exercises CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;

-- ============================================
-- USERS
-- ============================================
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    username    VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'ROLE_USER'
                CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN')),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role  ON users(role);

-- ============================================
-- REFRESH TOKENS
-- ============================================
CREATE TABLE refresh_tokens (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       VARCHAR(512) NOT NULL UNIQUE,
    expires_at  TIMESTAMP    NOT NULL,
    is_revoked  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user   ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token  ON refresh_tokens(token);

-- ============================================
-- CATEGORIES
-- ============================================
CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon_name   VARCHAR(50),
    sort_order  INTEGER      NOT NULL DEFAULT 0
);

INSERT INTO categories (name, description, icon_name, sort_order) VALUES
    ('Сон',         'Медитации для улучшения качества сна',      'moon',       1),
    ('Тревога',     'Техники снижения тревожности',              'calm',       2),
    ('Фокус',       'Практики для концентрации внимания',        'focus',      3),
    ('Стресс',      'Снятие стресса и напряжения',               'stress',     4),
    ('Осознанность','Практики mindfulness для каждого дня',      'mindful',    5),
    ('Энергия',     'Утренние медитации для заряда энергии',     'energy',     6);

-- ============================================
-- MEDITATIONS
-- ============================================
CREATE TABLE meditations (
    id               BIGSERIAL PRIMARY KEY,
    category_id      BIGINT       NOT NULL REFERENCES categories(id),
    title            VARCHAR(200) NOT NULL,
    description      TEXT         NOT NULL,
    content          TEXT         NOT NULL,
    duration_minutes INTEGER      NOT NULL CHECK (duration_minutes > 0),
    type             VARCHAR(30)  NOT NULL
                     CHECK (type IN ('GUIDED','BREATHING','BODY_SCAN',
                                     'VISUALIZATION','MINDFULNESS')),
    difficulty       VARCHAR(20)  NOT NULL
                     CHECK (difficulty IN ('BEGINNER','INTERMEDIATE','ADVANCED')),
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_meditations_category   ON meditations(category_id);
CREATE INDEX idx_meditations_type       ON meditations(type);
CREATE INDEX idx_meditations_difficulty ON meditations(difficulty);

-- Полнотекстовый поиск
CREATE INDEX idx_meditations_fts ON meditations
    USING gin(to_tsvector('russian', title || ' ' || description));

-- ============================================
-- MEDITATION SESSIONS
-- ============================================
CREATE TABLE meditation_sessions (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    meditation_id    BIGINT      NOT NULL REFERENCES meditations(id),
    started_at       TIMESTAMP   NOT NULL,
    completed_at     TIMESTAMP,
    duration_seconds INTEGER     NOT NULL CHECK (duration_seconds >= 0),
    completed        BOOLEAN     NOT NULL DEFAULT FALSE,
    sync_pending     BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sessions_user      ON meditation_sessions(user_id);
CREATE INDEX idx_sessions_meditation ON meditation_sessions(meditation_id);
CREATE INDEX idx_sessions_date      ON meditation_sessions(started_at);
CREATE INDEX idx_sessions_sync      ON meditation_sessions(sync_pending)
    WHERE sync_pending = TRUE;

-- ============================================
-- MOOD ENTRIES
-- ============================================
CREATE TABLE mood_entries (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    score        SMALLINT     NOT NULL CHECK (score BETWEEN 1 AND 10),
    note         VARCHAR(500),
    tags         VARCHAR(200),
    recorded_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    sync_pending BOOLEAN      NOT NULL DEFAULT FALSE,
    UNIQUE (user_id, DATE(recorded_at))
);

CREATE INDEX idx_mood_user ON mood_entries(user_id);
CREATE INDEX idx_mood_date ON mood_entries(recorded_at);

-- ============================================
-- USER PROGRESS
-- ============================================
CREATE TABLE user_progress (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT  NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    total_sessions   INTEGER NOT NULL DEFAULT 0,
    total_minutes    INTEGER NOT NULL DEFAULT 0,
    current_streak   INTEGER NOT NULL DEFAULT 0,
    longest_streak   INTEGER NOT NULL DEFAULT 0,
    last_session_date DATE,
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================
-- BREATHING EXERCISES
-- ============================================
CREATE TABLE breathing_exercises (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    inhale_seconds  INTEGER NOT NULL CHECK (inhale_seconds > 0),
    hold_seconds    INTEGER NOT NULL DEFAULT 0,
    exhale_seconds  INTEGER NOT NULL CHECK (exhale_seconds > 0),
    hold2_seconds   INTEGER NOT NULL DEFAULT 0,
    cycles          INTEGER NOT NULL DEFAULT 4,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO breathing_exercises
    (name, description, inhale_seconds, hold_seconds, exhale_seconds, cycles)
VALUES
    ('4-7-8',          'Техника для быстрого расслабления',     4, 7, 8,  4),
    ('Box Breathing',  'Квадратное дыхание для концентрации',   4, 4, 4,  8),
    ('Диафрагмальное', 'Глубокое брюшное дыхание',              5, 2, 7,  6);