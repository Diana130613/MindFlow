package ru.mindflow.app.control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mindflow.app.garden.GardenProgress
import ru.mindflow.app.mediator.IGardenRepository
import java.time.LocalDate

data class GardenUiState(
    val level: Int = 1,
    val levelName: String = "Росток 🌱",
    val flowers: Int = 0,
    val totalMinutes: Long = 0L,
    val streakDays: Int = 0,
    val progressToNext: Float = 0f,
    val nextLevelMinutes: Long = 60L,
    val hasGoldenFlower: Boolean = false,   // 7-day streak
    val hasRainbowFlower: Boolean = false,  // 30-day streak
    val unlockedDecorations: Set<String> = emptySet()
)

class GardenViewModel(
    private val repo: IGardenRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GardenUiState())
    val uiState: StateFlow<GardenUiState> = _uiState

    init {
        viewModelScope.launch {
            repo.observe()
                .filterNotNull()
                .collect { entity -> _uiState.value = entity.toUiState() }
        }
    }

    fun recordMeditation(minutes: Long) {
        if (minutes <= 0) return
        viewModelScope.launch {
            val current = repo.get()

            val today     = LocalDate.now().toString()
            val yesterday = LocalDate.now().minusDays(1).toString()
            val newStreak = when (current.lastPracticeDate) {
                today     -> current.currentStreakDays
                yesterday -> current.currentStreakDays + 1
                else      -> 1
            }

            val newMinutes  = current.totalMeditationMinutes + minutes
            val newLevel    = GardenProgress.levelFor(newMinutes)
            val newFlowers  = GardenProgress.flowersFor(newMinutes)
            val newDecos    = GardenProgress.decorationsFor(newStreak, newMinutes)

            repo.save(
                current.copy(
                    treeLevel               = newLevel,
                    flowersCount            = newFlowers,
                    totalMeditationMinutes  = newMinutes,
                    currentStreakDays       = newStreak,
                    lastPracticeDate        = today,
                    unlockedDecorations     = newDecos.joinToString(",")
                )
            )
        }
    }

    private fun ru.mindflow.app.foundation.local.entity.GardenEntity.toUiState(): GardenUiState {
        val decos = if (unlockedDecorations.isBlank()) emptySet()
                    else unlockedDecorations.split(",").toSet()
        return GardenUiState(
            level             = treeLevel,
            levelName         = GardenProgress.levelName(treeLevel),
            flowers           = flowersCount,
            totalMinutes      = totalMeditationMinutes,
            streakDays        = currentStreakDays,
            progressToNext    = GardenProgress.progressToNext(totalMeditationMinutes, treeLevel),
            nextLevelMinutes  = GardenProgress.minutesForNextLevel(treeLevel),
            hasGoldenFlower   = currentStreakDays >= 7,
            hasRainbowFlower  = currentStreakDays >= 30,
            unlockedDecorations = decos
        )
    }
}
