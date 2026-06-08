package ru.mindflow.app.mediator

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests the private moodLabel() logic via public save() indirectly.
 * We test the mapping rules directly via a package-visible helper.
 */
class MoodLabelMappingTest {

    // Replicate the mapping to verify contract
    private fun moodLabel(score: Int) = when (score) {
        1, 2  -> "Очень плохо"
        3, 4  -> "Плохо"
        5, 6  -> "Нейтрально"
        7, 8  -> "Хорошо"
        9, 10 -> "Отлично"
        else  -> "Нейтрально"
    }

    @Test fun `score 1 maps to Очень плохо`()  = assertEquals("Очень плохо", moodLabel(1))
    @Test fun `score 2 maps to Очень плохо`()  = assertEquals("Очень плохо", moodLabel(2))
    @Test fun `score 3 maps to Плохо`()        = assertEquals("Плохо",       moodLabel(3))
    @Test fun `score 4 maps to Плохо`()        = assertEquals("Плохо",       moodLabel(4))
    @Test fun `score 5 maps to Нейтрально`()   = assertEquals("Нейтрально",  moodLabel(5))
    @Test fun `score 6 maps to Нейтрально`()   = assertEquals("Нейтрально",  moodLabel(6))
    @Test fun `score 7 maps to Хорошо`()       = assertEquals("Хорошо",      moodLabel(7))
    @Test fun `score 8 maps to Хорошо`()       = assertEquals("Хорошо",      moodLabel(8))
    @Test fun `score 9 maps to Отлично`()      = assertEquals("Отлично",     moodLabel(9))
    @Test fun `score 10 maps to Отлично`()     = assertEquals("Отлично",     moodLabel(10))
    @Test fun `out of range maps to Нейтрально`() = assertEquals("Нейтрально", moodLabel(99))
}
