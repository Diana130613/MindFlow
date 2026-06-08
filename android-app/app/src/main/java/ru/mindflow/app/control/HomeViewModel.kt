package ru.mindflow.app.control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mindflow.app.entity.MoodEntry
import ru.mindflow.app.entity.User
import ru.mindflow.app.mediator.IAuthRepository
import ru.mindflow.app.mediator.IMoodRepository

data class HomeUiState(
    val user: User? = null,
    val todayMood: MoodEntry? = null,
    val weeklyAverage: Double = 0.0,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val authRepository: IAuthRepository,
    private val moodRepository: IMoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val user     = authRepository.getCurrentUser()
            val mood     = runCatching { moodRepository.getTodayEntry() }.getOrNull()
            val average  = runCatching { moodRepository.getAverage(7) }.getOrDefault(0.0)
            _uiState.value = HomeUiState(user = user, todayMood = mood,
                weeklyAverage = average, isLoading = false)
        }
    }
}
