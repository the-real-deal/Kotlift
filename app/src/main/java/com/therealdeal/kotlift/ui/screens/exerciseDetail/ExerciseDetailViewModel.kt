package com.therealdeal.kotlift.ui.screens.exerciseDetail

import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.data.repository.ExerciseDetailRepository
import com.therealdeal.kotlift.model.ExerciseDetail
import com.therealdeal.kotlift.ui.baseAuthentication.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed interface ExerciseDetailUiState {
    data object Loading : ExerciseDetailUiState
    data class Success(val exercise: ExerciseDetail) : ExerciseDetailUiState
    data class Error(val message: String) : ExerciseDetailUiState
}

class ExerciseDetailViewModel(
    authRepository: AuthRepository,
    private val exerciseDetailRepository: ExerciseDetailRepository,
    private val exerciseId: String
): BaseViewModel(authRepository)  {

    private val _uiState = MutableStateFlow<ExerciseDetailUiState>(ExerciseDetailUiState.Loading)
    val uiState: StateFlow<ExerciseDetailUiState> = _uiState.asStateFlow()

    init {
        loadExerciseDetail()
    }

    fun loadExerciseDetail() {
        viewModelScope.launch {
            _uiState.value = ExerciseDetailUiState.Loading
            exerciseDetailRepository.getExerciseDetail(exerciseId)
                .onSuccess { exercise ->
                    _uiState.value = ExerciseDetailUiState.Success(exercise)
                }
                .onFailure { error ->
                    _uiState.value = ExerciseDetailUiState.Error(
                        error.message ?: "Failed to load exercise"
                    )
                }
        }
    }
}