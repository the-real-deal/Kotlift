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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface WorkoutsUiState {
    data object Loading : WorkoutsUiState
    data class Success(val workouts: List<Workout>) : WorkoutsUiState
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
        // Osserva il flow reattivo del repository
        workoutRepository.getMyWorkoutsFlow()
            .onEach { result ->
                result
                    .onSuccess { workouts ->
                        _allWorkouts.value = workouts
                    }
                    .onFailure { error ->
                        _uiState.value = WorkoutsUiState.Error(error.message ?: "Error")
                    }
            }
            .launchIn(viewModelScope)

        // Combina lista + searchQuery per filtrare
        combine(_allWorkouts, _searchQuery.debounce(300)) { workouts, query ->
            val filtered = if (query.isBlank()) workouts
            else workouts.filter { it.name.contains(query, ignoreCase = true) }
            _uiState.value = WorkoutsUiState.Success(filtered)
        }.launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.update { query }
    }

    fun loadWorkouts() {
        viewModelScope.launch {
            _uiState.value = WorkoutsUiState.Loading
            workoutRepository.getMyWorkouts()
                .onSuccess { _allWorkouts.value = it }
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