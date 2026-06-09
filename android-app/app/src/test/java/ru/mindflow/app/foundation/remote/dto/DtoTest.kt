package ru.mindflow.app.foundation.remote.dto

import org.junit.Assert.*
import org.junit.Test

// ── Auth DTOs ─────────────────────────────────────────────────────────────────

class LoginRequestTest {
    @Test fun `stores email and password`() {
        val req = LoginRequest("user@test.com", "secret")
        assertEquals("user@test.com", req.email)
        assertEquals("secret", req.password)
    }

    @Test fun `equality based on fields`() {
        assertEquals(LoginRequest("a@b.com", "pass"), LoginRequest("a@b.com", "pass"))
    }

    @Test fun `different passwords not equal`() {
        assertNotEquals(LoginRequest("a@b.com", "pass1"), LoginRequest("a@b.com", "pass2"))
    }
}

class RegisterRequestTest {
    @Test fun `stores all three fields`() {
        val req = RegisterRequest("user@test.com", "secret", "Alice")
        assertEquals("user@test.com", req.email)
        assertEquals("secret", req.password)
        assertEquals("Alice", req.name)
    }

    @Test fun `equality based on all fields`() {
        assertEquals(
            RegisterRequest("a@b.com", "pass", "Bob"),
            RegisterRequest("a@b.com", "pass", "Bob")
        )
    }
}

class AuthResponseTest {
    @Test fun `stores all fields`() {
        val resp = AuthResponse("access123", "refresh456", "user@test.com", "Alice", "ROLE_USER")
        assertEquals("access123", resp.accessToken)
        assertEquals("refresh456", resp.refreshToken)
        assertEquals("user@test.com", resp.email)
        assertEquals("Alice", resp.name)
        assertEquals("ROLE_USER", resp.role)
    }

    @Test fun `admin role stored correctly`() {
        val resp = AuthResponse("tok", "ref", "admin@test.com", "Admin", "ROLE_ADMIN")
        assertEquals("ROLE_ADMIN", resp.role)
    }
}

// ── Mood DTOs ─────────────────────────────────────────────────────────────────

class MoodEntryRequestTest {
    @Test fun `note defaults to null`() = assertNull(MoodEntryRequest(5).note)
    @Test fun `score stored correctly`() = assertEquals(7, MoodEntryRequest(7).score)
    @Test fun `note can be provided`() = assertEquals("great day", MoodEntryRequest(9, "great day").note)

    @Test fun `equality based on fields`() {
        assertEquals(MoodEntryRequest(5, null), MoodEntryRequest(5, null))
    }

    @Test fun `different scores not equal`() {
        assertNotEquals(MoodEntryRequest(5), MoodEntryRequest(6))
    }
}

class MoodEntryDtoTest {
    @Test fun `stores all fields`() {
        val dto = MoodEntryDto(1L, 7, "note", "Хорошо", "2026-06-01")
        assertEquals(1L, dto.id)
        assertEquals(7, dto.score)
        assertEquals("note", dto.note)
        assertEquals("Хорошо", dto.moodLabel)
        assertEquals("2026-06-01", dto.recordedAt)
    }

    @Test fun `note can be null`() = assertNull(MoodEntryDto(1L, 5, null, "Нейтрально", "2026-01-01").note)

    @Test fun `equality based on all fields`() {
        assertEquals(
            MoodEntryDto(1L, 7, null, "Хорошо", "2026-01-01"),
            MoodEntryDto(1L, 7, null, "Хорошо", "2026-01-01")
        )
    }
}

// ── Meditation DTO ────────────────────────────────────────────────────────────

class MeditationDtoTest {
    @Test fun `stores required fields`() {
        val dto = MeditationDto(1L, "Медитация", null, null, null, null, null, null)
        assertEquals(1L, dto.id)
        assertEquals("Медитация", dto.title)
    }

    @Test fun `all optional fields can be null`() {
        val dto = MeditationDto(1L, "Title", null, null, null, null, null, null)
        assertNull(dto.description)
        assertNull(dto.durationMinutes)
        assertNull(dto.audioUrl)
        assertNull(dto.difficultyLevel)
        assertNull(dto.categoryName)
    }

    @Test fun `stores all fields correctly`() {
        val dto = MeditationDto(2L, "Дыхание", "Описание", 10, "audio.mp3", "img.png", "BEGINNER", "Дыхание")
        assertEquals(2L, dto.id)
        assertEquals(10, dto.durationMinutes)
        assertEquals("BEGINNER", dto.difficultyLevel)
        assertEquals("Дыхание", dto.categoryName)
    }

    @Test fun `equality based on all fields`() {
        assertEquals(
            MeditationDto(1L, "Test", null, 5, null, null, "BEGINNER", null),
            MeditationDto(1L, "Test", null, 5, null, null, "BEGINNER", null)
        )
    }
}