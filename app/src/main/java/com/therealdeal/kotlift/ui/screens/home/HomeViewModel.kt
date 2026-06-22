package com.therealdeal.kotlift.ui.screens.home

import androidx.lifecycle.ViewModel
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.model.Profile
import com.therealdeal.kotlift.model.Session
import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.SessionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val profile: Profile,
        val latestSessions: List<Session>
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val profileDeferred = async { authRepository.getCurrentUser() }
            val sessionsDeferred = async { sessionRepository.getLatestSessions() }

            val profile = profileDeferred.await()
            val sessionsResult = sessionsDeferred.await()

            if (profile == null) {
                _uiState.value = HomeUiState.Error("Failed to load profile")
                return@launch
            }

            sessionsResult
                .onSuccess { sessions ->
                    _uiState.value = HomeUiState.Success(
                        profile = profile,
                        latestSessions = sessions
                    )
                }
                .onFailure { error ->
                    _uiState.value = HomeUiState.Error(
                        error.message ?: "Failed to load sessions"
                    )
                }
        }
    }
}