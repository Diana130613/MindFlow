package ru.mindflow.app.control

import org.junit.Assert.*
import org.junit.Test

class GardenUiStateTest {

    @Test fun `default level is 1`() = assertEquals(1, GardenUiState().level)
    @Test fun `default flowers is 0`() = assertEquals(0, GardenUiState().flowers)
    @Test fun `default totalMinutes is 0`() = assertEquals(0L, GardenUiState().totalMinutes)
    @Test fun `default streakDays is 0`() = assertEquals(0, GardenUiState().streakDays)
    @Test fun `default progressToNext is 0`() = assertEquals(0f, GardenUiState().progressToNext, 0f)
    @Test fun `default nextLevelMinutes is 60`() = assertEquals(60L, GardenUiState().nextLevelMinutes)
    @Test fun `default hasGoldenFlower is false`() = assertFalse(GardenUiState().hasGoldenFlower)
    @Test fun `default hasRainbowFlower is false`() = assertFalse(GardenUiState().hasRainbowFlower)
    @Test fun `default unlockedDecorations is empty`() = assertTrue(GardenUiState().unlockedDecorations.isEmpty())

    @Test fun `copy changes only specified fields`() {
        val state = GardenUiState().copy(level = 4, flowers = 8, streakDays = 5)
        assertEquals(4, state.level)
        assertEquals(8, state.flowers)
        assertEquals(5, state.streakDays)
        assertEquals(0L, state.totalMinutes)
    }

    @Test fun `equality based on all fields`() {
        assertEquals(GardenUiState(), GardenUiState())
    }

    @Test fun `states with different levels are not equal`() {
        assertNotEquals(GardenUiState(level = 1), GardenUiState(level = 2))
    }

    @Test fun `state with decorations differs from default`() {
        val withDecos = GardenUiState(unlockedDecorations = setOf("stones"))
        assertNotEquals(GardenUiState(), withDecos)
    }
}
