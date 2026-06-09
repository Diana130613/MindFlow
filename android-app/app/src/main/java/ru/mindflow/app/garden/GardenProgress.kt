package ru.mindflow.app.garden

/** Pure calculation functions — no Android dependencies, fully unit-testable. */
object GardenProgress {

    /** Minutes required to reach each level (index = level-1). */
    val LEVEL_THRESHOLDS = longArrayOf(0, 60, 180, 360, 600, 900, 1300, 1800, 2500, 3500)

    fun levelFor(minutes: Long): Int {
        for (i in LEVEL_THRESHOLDS.indices.reversed()) {
            if (minutes >= LEVEL_THRESHOLDS[i]) return (i + 1).coerceAtMost(10)
        }
        return 1
    }

    /** One flower per 30 min, max 15. */
    fun flowersFor(minutes: Long): Int = (minutes / 30).coerceAtMost(15).toInt()

    /** 0..1 progress from current level threshold to next. */
    fun progressToNext(minutes: Long, level: Int): Float {
        if (level >= 10) return 1f
        val lo = LEVEL_THRESHOLDS[level - 1]
        val hi = LEVEL_THRESHOLDS[level]
        return ((minutes - lo).toFloat() / (hi - lo)).coerceIn(0f, 1f)
    }

    fun minutesForNextLevel(level: Int): Long =
        if (level >= 10) LEVEL_THRESHOLDS[9] else LEVEL_THRESHOLDS[level]

    fun levelName(level: Int): String = when (level) {
        1    -> "Росток 🌱"
        2    -> "Саженец 🌿"
        3    -> "Деревце 🌲"
        4    -> "Цветущее дерево 🌸"
        5    -> "Большое дерево 🌳"
        6    -> "Могучее дерево 🌳"
        7    -> "Вековой дуб 🌳"
        8    -> "Золотое дерево ✨"
        9    -> "Светящееся дерево ✨"
        else -> "Легендарное дерево 🏆"
    }

    fun decorationsFor(streakDays: Int, totalMinutes: Long): Set<String> = buildSet {
        if (streakDays >= 3)          add("stones")
        if (streakDays >= 10)         add("bench")
        if (totalMinutes >= 3000)     add("lantern")
        if (totalMinutes >= 6000)     add("stream")
        if (streakDays >= 30)         add("bird")
    }
}
