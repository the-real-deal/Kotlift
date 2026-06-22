package com.therealdeal.kotlift.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.therealdeal.kotlift.model.Stats

sealed interface StatsUiState {
    data object Loading : StatsUiState
    data class Success(val stats: Stats) : StatsUiState
    data class Error(val message: String) : StatsUiState
}


class StatsViewModel(
    private val statsRepository: StatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
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