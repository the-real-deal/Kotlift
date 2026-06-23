package com.therealdeal.kotlift.ui.screens.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.ExerciseLibraryRepository
import com.therealdeal.kotlift.model.Exercise
import com.therealdeal.kotlift.model.ExerciseFilters
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExercisesUiState(
    val allExercises: List<Exercise> = emptyList(),
    val filteredExercises: List<Exercise> = emptyList(),
    val muscles: List<String> = emptyList(),
    val bodyParts: List<String> = emptyList(),
    val equipments: List<String> = emptyList(),
    val filters: ExerciseFilters = ExerciseFilters(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasNextPage: Boolean = false,
    val error: String? = null,
    val nextCursor: String? = null
)
@OptIn(FlowPreview::class)
class ExercisesViewModel(
    private val exerciseLibraryRepository: ExerciseLibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExercisesUiState())
    val uiState: StateFlow<ExercisesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadMuscles()
        loadExercises()

        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    _uiState.update { it.copy(searchQuery = query) }
                    applyFilters()
                }
        }
    }

    private fun loadMuscles() {
        viewModelScope.launch {
            exerciseLibraryRepository.getMuscles()
                .onSuccess { muscles ->
                    _uiState.update { it.copy(muscles = muscles) }
                }
        }
    }

    fun loadExercises() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null, allExercises = emptyList(), filteredExercises = emptyList(), nextCursor = null)
            }
            exerciseLibraryRepository.getExercises()
                .onSuccess { page ->
                    val exercises = page.exercises
                    _uiState.update {
                        it.copy(
                            allExercises = exercises,
                            filteredExercises = exercises,
                            hasNextPage = page.hasNextPage,
                            nextCursor = page.nextCursor,
                            isLoading = false,
                            bodyParts = exercises.flatMap { e -> e.bodyParts }.distinct().sorted(),
                            equipments = exercises.flatMap { e -> e.equipment }.distinct().sorted()
                        )
                    }
                    applyFilters()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message ?: "Failed to load exercises") }
                }
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (!state.hasNextPage || state.isLoadingMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            exerciseLibraryRepository.getExercises(cursor = state.nextCursor)
                .onSuccess { page ->
                    val newAll = state.allExercises + page.exercises
                    _uiState.update {
                        it.copy(
                            allExercises = newAll,
                            hasNextPage = page.hasNextPage,
                            nextCursor = page.nextCursor,
                            isLoadingMore = false,
                            bodyParts = newAll.flatMap { e -> e.bodyParts }.distinct().sorted(),
                            equipments = newAll.flatMap { e -> e.equipment }.distinct().sorted()
                        )
                    }
                    applyFilters()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoadingMore = false, error = error.message ?: "Failed to load more") }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQuery.value = query
    }

    fun applyFilters(filters: ExerciseFilters = _uiState.value.filters) {
        _uiState.update { state ->
            val query = state.searchQuery.trim().lowercase()
            val filtered = state.allExercises.filter { exercise ->
                val matchesSearch = query.isEmpty() || exercise.name.lowercase().startsWith(query)
                val matchesMuscle = filters.targetMuscle == null || exercise.targetMuscles.any { it.equals(filters.targetMuscle, ignoreCase = true) }
                val matchesBodyPart = filters.bodyPart == null || exercise.bodyParts.any { it.equals(filters.bodyPart, ignoreCase = true) }
                val matchesEquipment = filters.equipment == null || exercise.equipment.any { it.equals(filters.equipment, ignoreCase = true) }
                matchesSearch && matchesMuscle && matchesBodyPart && matchesEquipment
            }
            state.copy(filters = filters, filteredExercises = filtered)
        }
    }

    fun clearFilters() {
        applyFilters(ExerciseFilters())
    }
}