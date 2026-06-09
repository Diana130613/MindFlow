package ru.mindflow.app.control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mindflow.app.entity.User
import ru.mindflow.app.mediator.IAuthRepository

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val loggedOut: Boolean = false,
    val daysWithUs: Long = 0
)

class ProfileViewModel(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState(isLoading = true)
            val user = authRepository.getCurrentUser()
            val joinDate = authRepository.getJoinDateMillis()
            val days = if (joinDate != null) {
                (System.currentTimeMillis() - joinDate) / (1000L * 60 * 60 * 24)
            } else 0L
            _uiState.value = ProfileUiState(user = user, isLoading = false, daysWithUs = days)
        }
    }

    fun updateName(name: String) {
        viewModelScope.launch {
            authRepository.updateName(name)
            val updatedUser = _uiState.value.user?.copy(name = name)
            _uiState.value = _uiState.value.copy(user = updatedUser)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = ProfileUiState(loggedOut = true)
        }
    }
}
