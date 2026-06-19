package com.therealdeal.kotlift.ui.screens.workoutDetail

sealed interface WorkoutDetailUiState {
    object Loading : WorkoutDetailUiState
    data class Error(val message: String) : WorkoutDetailUiState
    data class Success(val message: String) : WorkoutDetailUiState
}
