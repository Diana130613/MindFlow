package ru.mindflow.backend.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoodEntryEntityTest {

    private MoodEntry entry(int score) {
        return MoodEntry.builder().score(score).build();
    }

    @Test
    void getMoodLabel_score9and10_returnsОтлично() {
        assertEquals("Отлично", entry(9).getMoodLabel());
        assertEquals("Отлично", entry(10).getMoodLabel());
    }

    @Test
    void getMoodLabel_score7and8_returnsХорошо() {
        assertEquals("Хорошо", entry(7).getMoodLabel());
        assertEquals("Хорошо", entry(8).getMoodLabel());
    }

    @Test
    void getMoodLabel_score5and6_returnsНормально() {
        assertEquals("Нормально", entry(5).getMoodLabel());
        assertEquals("Нормально", entry(6).getMoodLabel());
    }

    @Test
    void getMoodLabel_score3and4_returnsПлохо() {
        assertEquals("Плохо", entry(3).getMoodLabel());
        assertEquals("Плохо", entry(4).getMoodLabel());
    }

    @Test
    void getMoodLabel_score1and2_returnsОченьПлохо() {
        assertEquals("Очень плохо", entry(1).getMoodLabel());
        assertEquals("Очень плохо", entry(2).getMoodLabel());
    }

    @Test
    void builder_setsAllFields() {
        MoodEntry entry = MoodEntry.builder()
                .id(1L).score(8).note("Хорошее настроение").build();
        assertEquals(1L, entry.getId());
        assertEquals(8, entry.getScore());
        assertEquals("Хорошее настроение", entry.getNote());
    }
}
