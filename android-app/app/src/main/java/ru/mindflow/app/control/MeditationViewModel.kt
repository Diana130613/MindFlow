package ru.mindflow.app.control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.mindflow.app.entity.Meditation
import ru.mindflow.app.mediator.IMeditationRepository

data class MeditationListUiState(
    val meditations: List<Meditation> = emptyList(),
    val selected: Meditation? = null,
    val searchQuery: String = "",
    val isSyncing: Boolean = false,
    val error: String? = null
)

class MeditationViewModel(
    private val repository: IMeditationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeditationListUiState())
    val uiState: StateFlow<MeditationListUiState> = _uiState

    val meditations: StateFlow<List<Meditation>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        sync()
    }

    fun sync() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true, error = null)
            runCatching { repository.sync() }
                .onFailure { _uiState.value = _uiState.value.copy(error = "Нет соединения. Показаны кэшированные данные.") }
            _uiState.value = _uiState.value.copy(isSyncing = false)
        }
    }

    fun selectMeditation(meditation: Meditation) {
        _uiState.value = _uiState.value.copy(selected = meditation)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selected = null)
    }
}
