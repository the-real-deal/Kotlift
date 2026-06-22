package com.therealdeal.kotlift.ui.screens.workoutDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.WorkoutDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.therealdeal.kotlift.model.WorkoutDetail

sealed interface WorkoutDetailUiState {
    data object Loading : WorkoutDetailUiState
    data class Success(val workout: WorkoutDetail) : WorkoutDetailUiState
    data class Error(val message: String) : WorkoutDetailUiState
}

class WorkoutDetailViewModel(
    private val workoutDetailRepository: WorkoutDetailRepository,
    private val workoutId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkoutDetailUiState>(WorkoutDetailUiState.Loading)
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.asStateFlow()

    init {
        loadWorkoutDetail()
    }

    fun loadWorkoutDetail() {
        viewModelScope.launch {
            _uiState.value = WorkoutDetailUiState.Loading
            workoutDetailRepository.getWorkoutDetail(workoutId)
                .onSuccess { workout ->
                    _uiState.value = WorkoutDetailUiState.Success(workout)
                }
                .onFailure { error ->
                    _uiState.value = WorkoutDetailUiState.Error(
                        error.message ?: "Failed to load workout"
                    )
                }
        }
    }
}