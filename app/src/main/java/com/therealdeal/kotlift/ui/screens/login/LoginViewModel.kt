package com.therealdeal.kotlift.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String         = "",
    val password: String      = "",
    val isLoading: Boolean    = false,
    val errorMessage: String? = null,
    val loggedInUser: Profile?   = null
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun login() {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Fill all fields!")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)

            authRepository.login(state.email.trim(), state.password)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(isLoading = false, loggedInUser = user)
                }
                .onFailure { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading    = false,
                        errorMessage = err.message ?: "Unknown Error"
                    )
                }
        }
    }
}