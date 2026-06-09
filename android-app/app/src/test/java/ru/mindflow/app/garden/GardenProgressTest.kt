package ru.mindflow.app.garden

import org.junit.Assert.*
import org.junit.Test

class GardenProgressTest {

    // ── Tree level ────────────────────────────────────────────────────────────

    @Test fun `level 1 at 0 minutes`()  = assertEquals(1, GardenProgress.levelFor(0))
    @Test fun `level 1 at 59 minutes`() = assertEquals(1, GardenProgress.levelFor(59))
    @Test fun `level 2 at 60 minutes`() = assertEquals(2, GardenProgress.levelFor(60))
    @Test fun `level 2 at 179 minutes`()= assertEquals(2, GardenProgress.levelFor(179))
    @Test fun `level 3 at 180 minutes`()= assertEquals(3, GardenProgress.levelFor(180))
    @Test fun `level 4 at 360 minutes`()= assertEquals(4, GardenProgress.levelFor(360))
    @Test fun `level 5 at 600 minutes`()= assertEquals(5, GardenProgress.levelFor(600))
    @Test fun `level 6 at 900 minutes`()= assertEquals(6, GardenProgress.levelFor(900))
    @Test fun `level 10 at 3500 minutes`()= assertEquals(10, GardenProgress.levelFor(3500))
    @Test fun `level 10 stays capped at 9999 minutes`() = assertEquals(10, GardenProgress.levelFor(9999))

    // ── Flowers ───────────────────────────────────────────────────────────────

    @Test fun `0 flowers at 0 minutes`()    = assertEquals(0,  GardenProgress.flowersFor(0))
    @Test fun `0 flowers at 29 minutes`()   = assertEquals(0,  GardenProgress.flowersFor(29))
    @Test fun `1 flower at 30 minutes`()    = assertEquals(1,  GardenProgress.flowersFor(30))
    @Test fun `2 flowers at 60 minutes`()   = assertEquals(2,  GardenProgress.flowersFor(60))
    @Test fun `3 flowers at 90 minutes`()   = assertEquals(3,  GardenProgress.flowersFor(90))
    @Test fun `15 flowers max at 450 minutes`() = assertEquals(15, GardenProgress.flowersFor(450))
    @Test fun `flowers capped at 15 for large input`() = assertEquals(15, GardenProgress.flowersFor(10000))

    // ── Progress bar ──────────────────────────────────────────────────────────

    @Test fun `progress is 0 at level start`() =
        assertEquals(0f, GardenProgress.progressToNext(0, 1), 0.001f)

    @Test fun `progress is 0_5 halfway through level 1`() =
        assertEquals(0.5f, GardenProgress.progressToNext(30, 1), 0.001f)

    @Test fun `progress reaches 1 at level threshold`() =
        assertEquals(1f, GardenProgress.progressToNext(60, 1), 0.001f)

    @Test fun `progress is 1 at max level`() =
        assertEquals(1f, GardenProgress.progressToNext(9999, 10), 0.001f)

    // ── Decorations ───────────────────────────────────────────────────────────

    @Test fun `no decorations at zero streak and zero minutes`() =
        assertTrue(GardenProgress.decorationsFor(0, 0).isEmpty())

    @Test fun `stones unlocked at 3-day streak`() =
        assertTrue(GardenProgress.decorationsFor(3, 0).contains("stones"))

    @Test fun `stones not unlocked at 2-day streak`() =
        assertFalse(GardenProgress.decorationsFor(2, 0).contains("stones"))

    @Test fun `bench unlocked at 10-day streak`() =
        assertTrue(GardenProgress.decorationsFor(10, 0).contains("bench"))

    @Test fun `bench includes stones at 10 days`() =
        assertTrue(GardenProgress.decorationsFor(10, 0).containsAll(setOf("stones", "bench")))

    @Test fun `lantern unlocked at 3000 minutes`() =
        assertTrue(GardenProgress.decorationsFor(0, 3000).contains("lantern"))

    @Test fun `lantern not unlocked at 2999 minutes`() =
        assertFalse(GardenProgress.decorationsFor(0, 2999).contains("lantern"))

    @Test fun `stream unlocked at 6000 minutes`() =
        assertTrue(GardenProgress.decorationsFor(0, 6000).contains("stream"))

    @Test fun `bird unlocked at 30-day streak`() =
        assertTrue(GardenProgress.decorationsFor(30, 0).contains("bird"))

    @Test fun `all decorations unlocked at max conditions`() {
        val all = GardenProgress.decorationsFor(30, 6000)
        assertTrue(all.containsAll(setOf("stones", "bench", "lantern", "stream", "bird")))
    }

    // ── Level name ────────────────────────────────────────────────────────────

    @Test fun `level 1 name contains rosток`() =
        assertTrue(GardenProgress.levelName(1).contains("Росток"))

    @Test fun `level 10 name contains legendary`() =
        assertTrue(GardenProgress.levelName(10).contains("Легендарное"))

    // ── Next-level minutes ────────────────────────────────────────────────────

    @Test fun `next level minutes for level 1 is 60`() =
        assertEquals(60L, GardenProgress.minutesForNextLevel(1))

    @Test fun `next level minutes for level 10 returns last threshold`() =
        assertEquals(GardenProgress.LEVEL_THRESHOLDS[9], GardenProgress.minutesForNextLevel(10))
}