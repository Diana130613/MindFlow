package ru.mindflow.app.entity

import org.junit.Assert.assertEquals
import org.junit.Test

class MoodEntryTest {

    private fun entry(score: Int) = MoodEntry(1L, score, null, "", "", false)

    @Test fun `emoji returns sad face for score 1`() = assertEquals("😢", entry(1).emoji())
    @Test fun `emoji returns sad face for score 2`() = assertEquals("😢", entry(2).emoji())
    @Test fun `emoji returns frown for score 3`()    = assertEquals("😕", entry(3).emoji())
    @Test fun `emoji returns frown for score 4`()    = assertEquals("😕", entry(4).emoji())
    @Test fun `emoji returns neutral for score 5`()  = assertEquals("😐", entry(5).emoji())
    @Test fun `emoji returns neutral for score 6`()  = assertEquals("😐", entry(6).emoji())
    @Test fun `emoji returns smile for score 7`()    = assertEquals("🙂", entry(7).emoji())
    @Test fun `emoji returns smile for score 8`()    = assertEquals("🙂", entry(8).emoji())
    @Test fun `emoji returns happy for score 9`()    = assertEquals("😄", entry(9).emoji())
    @Test fun `emoji returns happy for score 10`()   = assertEquals("😄", entry(10).emoji())
}
