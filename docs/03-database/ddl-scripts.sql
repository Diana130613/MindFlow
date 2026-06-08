-- ============================================================
-- MindFlow — DDL-скрипты создания базы данных (PostgreSQL 15)
-- ============================================================

CREATE TABLE IF NOT EXISTS categories (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon_name   VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL    PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    name        VARCHAR(100) NOT NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'ROLE_USER'
                             CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN')),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS meditations (
    id               BIGSERIAL    PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    duration_minutes INTEGER      CHECK (duration_minutes > 0),
    audio_url        VARCHAR(500),
    image_url        VARCHAR(500),
    difficulty_level VARCHAR(20)  CHECK (difficulty_level IN ('BEGINNER','INTERMEDIATE','ADVANCED')),
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    category_id      BIGINT       REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS meditation_sessions (
    id               BIGSERIAL PRIMARY KEY,
    started_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at     TIMESTAMP,
    duration_seconds INTEGER   CHECK (duration_seconds >= 0),
    completed        BOOLEAN   NOT NULL DEFAULT FALSE,
    user_id          BIGINT    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    meditation_id    BIGINT    NOT NULL REFERENCES meditations(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS mood_entries (
    id          BIGSERIAL PRIMARY KEY,
    score       INTEGER   NOT NULL CHECK (score >= 1 AND score <= 10),
    note        TEXT,
    recorded_at TIMESTAMP NOT NULL DEFAULT NOW(),
    user_id     BIGINT    NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

-- Индексы для оптимизации запросов
CREATE INDEX IF NOT EXISTS idx_meditations_category ON meditations(category_id);
CREATE INDEX IF NOT EXISTS idx_meditations_active   ON meditations(active);
CREATE INDEX IF NOT EXISTS idx_sessions_user        ON meditation_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_mood_user_date       ON mood_entries(user_id, recorded_at DESC);

-- Начальные данные
INSERT INTO categories (name, description, icon_name) VALUES
    ('Сон',          'Медитации для улучшения качества сна',          'ic_moon'),
    ('Тревога',      'Техники снижения тревожности',                   'ic_calm'),
    ('Фокус',        'Практики для концентрации и продуктивности',     'ic_focus'),
    ('Стресс',       'Упражнения для снятия стресса',                  'ic_stress'),
    ('Осознанность', 'Базовые практики mindfulness',                   'ic_mindful')
ON CONFLICT (name) DO NOTHING;
