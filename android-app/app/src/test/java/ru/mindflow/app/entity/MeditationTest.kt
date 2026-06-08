package ru.mindflow.app.entity

import org.junit.Assert.assertEquals
import org.junit.Test

class MeditationTest {

    private fun meditation(duration: Int?, level: String?) =
        Meditation(1L, "Test", null, duration, null, null, level, null)

    @Test fun `formattedDuration returns minutes string`() {
        assertEquals("15 мин", meditation(15, null).formattedDuration())
    }

    @Test fun `formattedDuration returns dash when null`() {
        assertEquals("—", meditation(null, null).formattedDuration())
    }

    @Test fun `difficultyLabel maps BEGINNER`() {
        assertEquals("Начинающий", meditation(null, "BEGINNER").difficultyLabel())
    }

    @Test fun `difficultyLabel maps INTERMEDIATE`() {
        assertEquals("Средний", meditation(null, "INTERMEDIATE").difficultyLabel())
    }

    @Test fun `difficultyLabel maps ADVANCED`() {
        assertEquals("Продвинутый", meditation(null, "ADVANCED").difficultyLabel())
    }

    @Test fun `difficultyLabel returns raw value for unknown`() {
        assertEquals("EXPERT", meditation(null, "EXPERT").difficultyLabel())
    }

    @Test fun `difficultyLabel returns dash when null`() {
        assertEquals("—", meditation(null, null).difficultyLabel())
    }

    @Test fun `difficultyLabel is case insensitive`() {
        assertEquals("Начинающий", meditation(null, "beginner").difficultyLabel())
    }
}
