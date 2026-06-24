package com.therealdeal.kotlift.ui.screens.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.data.repository.WorkoutRepository
import com.therealdeal.kotlift.model.Workout
import com.therealdeal.kotlift.ui.baseAuthentication.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface WorkoutsUiState {
    data object Loading : WorkoutsUiState
    data class Success(val workouts: List<Workout>) : WorkoutsUiState
    data class Error(val message: String) : WorkoutsUiState
}

class WorkoutsViewModel(
    authRepository: AuthRepository,
    private val workoutRepository: WorkoutRepository
)  : BaseViewModel(authRepository) {

    private val _uiState = MutableStateFlow<WorkoutsUiState>(WorkoutsUiState.Loading)
    val uiState: StateFlow<WorkoutsUiState> = _uiState.asStateFlow()

    init {
        loadWorkouts()
    }

    fun loadWorkouts() {
        viewModelScope.launch {
            _uiState.value = WorkoutsUiState.Loading
            workoutRepository.getMyWorkouts()
                .onSuccess { workouts ->
                    _uiState.value = WorkoutsUiState.Success(workouts)
                }
                .onFailure { error ->
                    _uiState.value = WorkoutsUiState.Error(
                        error.message ?: "Error"
                    )
                }
        }
    }
}
