package com.therealdeal.kotlift.ui.screens.activeWorkout

import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.data.repository.SessionRepository
import com.therealdeal.kotlift.data.repository.WorkoutDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.therealdeal.kotlift.model.WorkoutDetail
import com.therealdeal.kotlift.ui.baseAuthentication.BaseViewModel

sealed interface ActiveWorkoutUiState {
    data object Loading : ActiveWorkoutUiState
    data class Success(val workout: WorkoutDetail) : ActiveWorkoutUiState
    data class Error(val message: String) : ActiveWorkoutUiState
}

class ActiveWorkoutViewModel(
    authRepository: AuthRepository,
    private val workoutDetailRepository: WorkoutDetailRepository,
    private val sessionRepository: SessionRepository,
    private val workoutId: String
): BaseViewModel(authRepository)  {

    private val _uiState = MutableStateFlow<ActiveWorkoutUiState>(ActiveWorkoutUiState.Loading)
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    init {
        loadWorkoutDetail()
    }

    fun loadWorkoutDetail() {
        viewModelScope.launch {
            _uiState.value = ActiveWorkoutUiState.Loading
            workoutDetailRepository.getWorkoutDetail(workoutId)
                .onSuccess { workout ->
                    _uiState.value = ActiveWorkoutUiState.Success(workout)
                }
                .onFailure { error ->
                    _uiState.value = ActiveWorkoutUiState.Error(
                        error.message ?: "Failed to load workout"
                    )
                }
        }
    }
}