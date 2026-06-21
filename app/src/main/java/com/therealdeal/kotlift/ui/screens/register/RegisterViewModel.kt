package com.therealdeal.kotlift.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUserUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    // Register state
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUserUiState())
    val uiState: StateFlow<RegisterUserUiState> = _uiState.asStateFlow()

    fun onUsernameChange(name: String) {
        _uiState.value = _uiState.value.copy( username = name, errorMessage = null)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy( email = email, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy( password = password, errorMessage = null)
    }

    fun onPasswordConfirmChange(password: String) {
        _uiState.value = _uiState.value.copy( confirmPassword = password, errorMessage = null)
    }

    /**
     * Starts the registration process
     */
    fun register(){
        val state = _uiState.value

        // clear error values
        _uiState.value = _uiState.value.copy( errorMessage = null)

        if (state.username.isBlank() || state.email.isBlank() || state.password.isBlank() || state.confirmPassword.isBlank()) {
            _uiState.value = _uiState.value.copy( errorMessage = "Please fill all the fields")
            return
        }

        if (state.password.length < 8) {
            _uiState.value = _uiState.value.copy( errorMessage = "Password must be at least 8 characters")
            return
        }

        if(state.password != state.confirmPassword) {
            _uiState.value = _uiState.value.copy( errorMessage = "Passwords are not matching")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy( isLoading = true, errorMessage = null)

            val result = authRepository.registerUser(state.username, state.email, state.password)

            result.onSuccess {
                _uiState.value = _uiState.value.copy( isLoading = false, isSuccess = true)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy( isLoading = false, errorMessage = err.message)
            }
        }

    }
}