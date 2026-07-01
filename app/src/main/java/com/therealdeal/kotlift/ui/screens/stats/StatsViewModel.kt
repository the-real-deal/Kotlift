package com.therealdeal.kotlift.ui.screens.stats


import com.therealdeal.kotlift.ui.baseAuthentication.BaseViewModel
import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.data.repository.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.therealdeal.kotlift.model.Stats
import kotlinx.coroutines.flow.update

sealed interface StatsUiState {
    data object Loading : StatsUiState
    data class Success(
        val stats: Stats,
        val isReloading: Boolean = false
    ) : StatsUiState
    data class Error(val message: String) : StatsUiState
}


class StatsViewModel(
    authRepository: AuthRepository,
    private val statsRepository: StatsRepository
) : BaseViewModel(authRepository) {

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun reload() {
        val currentState = _uiState.value
        if (currentState !is StatsUiState.Success) return

        viewModelScope.launch {
            _uiState.update {
                (it as? StatsUiState.Success)?.copy(isReloading = true) ?: it
            }
            statsRepository.getStats()
                .onSuccess { stats ->
                    _uiState.value = StatsUiState.Success(stats, isReloading = false)
                }
                .onFailure {
                    _uiState.update {
                        (it as? StatsUiState.Success)?.copy(isReloading = false) ?: it
                    }
                }
        }
    }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.value = StatsUiState.Loading
            statsRepository.getStats()
                .onSuccess { stats ->
                    _uiState.value = StatsUiState.Success(stats)
                }
                .onFailure { error ->
                    _uiState.value = StatsUiState.Error(
                        error.message ?: "Failed to load stats"
                    )
                }
        }
    }
}