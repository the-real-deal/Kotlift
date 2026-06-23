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
        fetchFirstPage()

        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    fetchFirstPage(searchQuery = query)
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

    private fun fetchFirstPage(
        filters: ExerciseFilters = _uiState.value.filters,
        searchQuery: String = _uiState.value.searchQuery
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            exerciseLibraryRepository.getExercises(
                targetMuscle = filters.targetMuscle,
                bodyPart = filters.bodyPart,
                equipment = filters.equipment,
                query = searchQuery.takeIf { it.isNotBlank() }
            ).onSuccess { page ->
                _uiState.update { state ->
                    val all = page.exercises
                    state.copy(
                        allExercises = all,
                        filteredExercises = all,
                        hasNextPage = page.hasNextPage,
                        nextCursor = page.nextCursor,
                        isLoading = false,
                        searchQuery = searchQuery,
                        filters = filters,
                        bodyParts = all.flatMap { it.bodyParts }.distinct().sorted(),
                        equipments = all.flatMap { it.equipment }.distinct().sorted()
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = error.message ?: "Failed to load exercises")
                }
            }
        }
    }

    fun loadExercises() = fetchFirstPage()

    fun loadNextPage() {
        val state = _uiState.value
        if (!state.hasNextPage || state.isLoadingMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            exerciseLibraryRepository.getExercises(
                cursor = state.nextCursor,
                targetMuscle = state.filters.targetMuscle,
                bodyPart = state.filters.bodyPart,
                equipment = state.filters.equipment,
                query = state.searchQuery.takeIf { it.isNotBlank() }
            ).onSuccess { page ->
                _uiState.update { state ->
                    val newAll = state.allExercises + page.exercises
                    state.copy(
                        allExercises = newAll,
                        filteredExercises = newAll,
                        hasNextPage = page.hasNextPage,
                        nextCursor = page.nextCursor,
                        isLoadingMore = false,
                        bodyParts = newAll.flatMap { it.bodyParts }.distinct().sorted(),
                        equipments = newAll.flatMap { it.equipment }.distinct().sorted()
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoadingMore = false, error = error.message ?: "Failed to load more")
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQuery.value = query
    }

    fun applyFilters(filters: ExerciseFilters) {
        fetchFirstPage(filters = filters)
    }

    fun clearFilters() = applyFilters(ExerciseFilters())
}