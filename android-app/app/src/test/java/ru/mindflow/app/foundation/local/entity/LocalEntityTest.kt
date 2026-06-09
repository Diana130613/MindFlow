package ru.mindflow.app.foundation.local.entity

import org.junit.Assert.*
import org.junit.Test

// ── GardenEntity ──────────────────────────────────────────────────────────────

class GardenEntityTest {

    @Test fun `default id is 1`() = assertEquals(1, GardenEntity().id)
    @Test fun `default treeLevel is 1`() = assertEquals(1, GardenEntity().treeLevel)
    @Test fun `default flowersCount is 0`() = assertEquals(0, GardenEntity().flowersCount)
    @Test fun `default totalMeditationMinutes is 0`() = assertEquals(0L, GardenEntity().totalMeditationMinutes)
    @Test fun `default currentStreakDays is 0`() = assertEquals(0, GardenEntity().currentStreakDays)
    @Test fun `default lastPracticeDate is empty`() = assertEquals("", GardenEntity().lastPracticeDate)
    @Test fun `default unlockedDecorations is empty string`() = assertEquals("", GardenEntity().unlockedDecorations)

    @Test fun `copy preserves unchanged fields`() {
        val entity = GardenEntity().copy(treeLevel = 3, totalMeditationMinutes = 200L)
        assertEquals(3, entity.treeLevel)
        assertEquals(200L, entity.totalMeditationMinutes)
        assertEquals(0, entity.flowersCount)
        assertEquals(1, entity.id)
    }

    @Test fun `two default entities are equal`() = assertEquals(GardenEntity(), GardenEntity())

    @Test fun `entities with different levels are not equal`() {
        assertNotEquals(GardenEntity(treeLevel = 1), GardenEntity(treeLevel = 2))
    }

    @Test fun `decorations string preserved`() {
        val entity = GardenEntity(unlockedDecorations = "stones,bench,lantern")
        assertEquals("stones,bench,lantern", entity.unlockedDecorations)
    }

    @Test fun `all fields stored correctly`() {
        val entity = GardenEntity(
            id = 1,
            treeLevel = 7,
            flowersCount = 12,
            totalMeditationMinutes = 900L,
            currentStreakDays = 15,
            lastPracticeDate = "2026-06-09",
            unlockedDecorations = "stones,bench"
        )
        assertEquals(7, entity.treeLevel)
        assertEquals(12, entity.flowersCount)
        assertEquals(900L, entity.totalMeditationMinutes)
        assertEquals(15, entity.currentStreakDays)
        assertEquals("2026-06-09", entity.lastPracticeDate)
    }
}

// ── MoodEntryEntity ───────────────────────────────────────────────────────────

class MoodEntryEntityTest {

    @Test fun `syncPending defaults to false`() =
        assertFalse(MoodEntryEntity(1L, 5, null, "Нейтрально", "2026-01-01").syncPending)

    @Test fun `syncPending can be set to true`() =
        assertTrue(MoodEntryEntity(1L, 5, null, "Нейтрально", "2026-01-01", true).syncPending)

    @Test fun `note can be null`() =
        assertNull(MoodEntryEntity(1L, 5, null, "Нейтрально", "2026-01-01").note)

    @Test fun `note is stored when provided`() =
        assertEquals("feeling good", MoodEntryEntity(1L, 8, "feeling good", "Хорошо", "2026-01-01").note)

    @Test fun `all fields stored correctly`() {
        val entity = MoodEntryEntity(42L, 7, "ok", "Хорошо", "2026-06-01", false)
        assertEquals(42L, entity.id)
        assertEquals(7, entity.score)
        assertEquals("Хорошо", entity.moodLabel)
        assertEquals("2026-06-01", entity.recordedAt)
    }

    @Test fun `equality based on all fields`() {
        val e1 = MoodEntryEntity(1L, 7, null, "Хорошо", "2026-01-01")
        val e2 = MoodEntryEntity(1L, 7, null, "Хорошо", "2026-01-01")
        assertEquals(e1, e2)
    }

    @Test fun `different ids means not equal`() {
        val e1 = MoodEntryEntity(1L, 7, null, "Хорошо", "2026-01-01")
        val e2 = MoodEntryEntity(2L, 7, null, "Хорошо", "2026-01-01")
        assertNotEquals(e1, e2)
    }
}

// ── MeditationEntity ──────────────────────────────────────────────────────────

class MeditationEntityTest {

    @Test fun `all optional fields can be null`() {
        val entity = MeditationEntity(1L, "Test", null, null, null, null, null, null)
        assertNull(entity.description)
        assertNull(entity.durationMinutes)
        assertNull(entity.audioUrl)
        assertNull(entity.imageUrl)
        assertNull(entity.difficultyLevel)
        assertNull(entity.categoryName)
    }

    @Test fun `required fields stored correctly`() {
        val entity = MeditationEntity(5L, "Дыхание", null, null, null, null, null, null)
        assertEquals(5L, entity.id)
        assertEquals("Дыхание", entity.title)
    }

    @Test fun `all fields stored correctly`() {
        val entity = MeditationEntity(
            id = 1L, title = "Медитация",
            description = "Описание", durationMinutes = 15,
            audioUrl = "http://audio", imageUrl = "http://img",
            difficultyLevel = "BEGINNER", categoryName = "Дыхание"
        )
        assertEquals(15, entity.durationMinutes)
        assertEquals("BEGINNER", entity.difficultyLevel)
        assertEquals("Дыхание", entity.categoryName)
    }

    @Test fun `equality based on all fields`() {
        val e1 = MeditationEntity(1L, "Test", null, 10, null, null, "BEGINNER", null)
        val e2 = MeditationEntity(1L, "Test", null, 10, null, null, "BEGINNER", null)
        assertEquals(e1, e2)
    }

    @Test fun `different durations means not equal`() {
        val e1 = MeditationEntity(1L, "Test", null, 10, null, null, null, null)
        val e2 = MeditationEntity(1L, "Test", null, 20, null, null, null, null)
        assertNotEquals(e1, e2)
    }
}