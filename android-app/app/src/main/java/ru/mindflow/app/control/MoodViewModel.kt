package ru.mindflow.app.control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.mindflow.app.entity.MoodEntry
import ru.mindflow.app.mediator.IMoodRepository

data class MoodUiState(
    val isSaving: Boolean = false,
    val savedEntry: MoodEntry? = null,
    val error: String? = null,
    val average: Double = 0.0
)

class MoodViewModel(
    private val repository: IMoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoodUiState())
    val uiState: StateFlow<MoodUiState> = _uiState

    val history: StateFlow<List<MoodEntry>> = repository.getHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        loadAverage()
    }

    fun saveMood(score: Int, note: String?) {
        _uiState.value = _uiState.value.copy(isSaving = true, error = null)
        viewModelScope.launch {
            runCatching { repository.save(score, note) }
                .onSuccess { entry ->
                    _uiState.value = _uiState.value.copy(isSaving = false, savedEntry = entry)
                    loadAverage()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false, error = it.message ?: "Ошибка сохранения настроения"
                    )
                }
        }
    }

    fun deleteMood(id: Long) {
        viewModelScope.launch {
            runCatching { repository.delete(id) }
        }
    }

    fun resetSaved() {
        _uiState.value = _uiState.value.copy(savedEntry = null)
    }

    private fun loadAverage() {
        viewModelScope.launch {
            val avg = runCatching { repository.getAverage(30) }.getOrDefault(0.0)
            _uiState.value = _uiState.value.copy(average = avg)
        }
    }
}
