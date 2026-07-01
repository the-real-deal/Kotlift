package com.therealdeal.kotlift.ui.screens.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.data.repository.ExerciseLibraryRepository
import com.therealdeal.kotlift.model.Exercise
import com.therealdeal.kotlift.model.ExerciseFilters
import com.therealdeal.kotlift.ui.baseAuthentication.BaseViewModel
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
    authRepository: AuthRepository,
    private val exerciseLibraryRepository: ExerciseLibraryRepository
) : BaseViewModel(authRepository) {

    private val _uiState = MutableStateFlow(ExercisesUiState())
    val uiState: StateFlow<ExercisesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadMuscles()
        loadBodyParts()
        loadEquipments()
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

    /**
     * Caricati UNA SOLA VOLTA all'avvio tramite endpoint dedicati, indipendenti
     * dai filtri correnti. NON vanno più ricalcolati da fetchFirstPage/loadNextPage:
     * era proprio quello il bug — le opzioni disponibili venivano derivate dai
     * risultati già filtrati, quindi collassavano al sottoinsieme selezionato
     * ogni volta che si applicava un filtro.
     */
    private fun loadBodyParts() {
        viewModelScope.launch {
            exerciseLibraryRepository.getBodyParts()
                .onSuccess { bodyParts ->
                    _uiState.update { it.copy(bodyParts = bodyParts) }
                }
        }
    }

    private fun loadEquipments() {
        viewModelScope.launch {
            exerciseLibraryRepository.getEquipments()
                .onSuccess { equipments ->
                    _uiState.update { it.copy(equipments = equipments) }
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
                targetMuscles = listOfNotNull(filters.targetMuscle),
                bodyParts = listOfNotNull(filters.bodyPart),
                equipments = listOfNotNull(filters.equipment),
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
                        filters = filters
                        // bodyParts/equipments NON toccati qui: arrivano solo da
                        // loadBodyParts()/loadEquipments(), caricati una sola volta.
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
                targetMuscles = listOfNotNull(state.filters.targetMuscle),
                bodyParts = listOfNotNull(state.filters.bodyPart),
                equipments = listOfNotNull(state.filters.equipment),
                query = state.searchQuery.takeIf { it.isNotBlank() }
            ).onSuccess { page ->
                _uiState.update { state ->
                    val newAll = state.allExercises + page.exercises
                    state.copy(
                        allExercises = newAll,
                        filteredExercises = newAll,
                        hasNextPage = page.hasNextPage,
                        nextCursor = page.nextCursor,
                        isLoadingMore = false
                        // bodyParts/equipments NON toccati qui, stesso motivo sopra.
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