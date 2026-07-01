package com.therealdeal.kotlift.ui.screens.workouts

import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.data.repository.WorkoutRepository
import com.therealdeal.kotlift.model.Workout
import com.therealdeal.kotlift.ui.baseAuthentication.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface WorkoutsUiState {
    data object Loading : WorkoutsUiState
    data class Success(
        val workouts: List<Workout>,
        val isReloading: Boolean = false) : WorkoutsUiState
    data class Error(val message: String) : WorkoutsUiState
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class WorkoutsViewModel(
    authRepository: AuthRepository,
    private val workoutRepository: WorkoutRepository
) : BaseViewModel(authRepository) {

    private val _uiState = MutableStateFlow<WorkoutsUiState>(WorkoutsUiState.Loading)
    val uiState: StateFlow<WorkoutsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allWorkouts = MutableStateFlow<List<Workout>>(emptyList())

    init {
        loadWorkouts()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.update { query }
    }

    fun reload() {
        val currentState = _uiState.value
        if (currentState !is WorkoutsUiState.Success) return

        viewModelScope.launch {
            _uiState.update {
                (it as? WorkoutsUiState.Success)?.copy(isReloading = true) ?: it
            }
            try {
                workoutRepository.getMyWorkouts()
                    .onSuccess {
                        _allWorkouts.value = it
                        _uiState.value = WorkoutsUiState.Success(workouts = it, isReloading = false)
                    }
                    .onFailure {
                        _uiState.value = WorkoutsUiState.Error(it.message ?: "Error")
                    }
            } catch (e: Exception) {
                _uiState.value = WorkoutsUiState.Error(e.message ?: "Error")
            }
        }
    }

    fun loadWorkouts() {
        viewModelScope.launch {
            _uiState.value = WorkoutsUiState.Loading
            workoutRepository.getMyWorkouts()
                .onSuccess {
                    _allWorkouts.value = it
                    _uiState.value = WorkoutsUiState.Success(workouts = it)
                }
                .onFailure { _uiState.value = WorkoutsUiState.Error(it.message ?: "Error") }
        }
    }

    fun deleteWorkout(workoutId: String) {
        viewModelScope.launch {
            workoutRepository.deleteWorkout(workoutId)
                .onFailure { _uiState.value = WorkoutsUiState.Error(it.message ?: "Delete failed") }
        }
    }
}