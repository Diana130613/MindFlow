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
    val loggedOut: Boolean = false
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
            _uiState.value = ProfileUiState(user = user, isLoading = false)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = ProfileUiState(loggedOut = true)
        }
    }
}
