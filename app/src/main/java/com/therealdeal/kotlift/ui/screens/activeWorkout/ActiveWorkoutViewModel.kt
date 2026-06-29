package com.therealdeal.kotlift.ui.screens.activeWorkout

import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.data.repository.ExerciseLibraryRepository
import com.therealdeal.kotlift.data.repository.SessionRepository
import com.therealdeal.kotlift.data.repository.WorkoutDetailRepository
import com.therealdeal.kotlift.model.ExerciseInWorkout
import com.therealdeal.kotlift.model.WorkoutDetail
import com.therealdeal.kotlift.ui.baseAuthentication.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface ActiveWorkoutUiState {
    data object Loading : ActiveWorkoutUiState
    data class Success(val workout: WorkoutDetail) : ActiveWorkoutUiState
    data class Error(val message: String) : ActiveWorkoutUiState
}

class ActiveWorkoutViewModel(
    authRepository: AuthRepository,
    private val workoutDetailRepository: WorkoutDetailRepository,
    private val exerciseLibraryRepository: ExerciseLibraryRepository,
    private val sessionRepository: SessionRepository,
    private val workoutId: String
) : BaseViewModel(authRepository) {

    private val _uiState = MutableStateFlow<ActiveWorkoutUiState>(ActiveWorkoutUiState.Loading)
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadWorkoutDetail()
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _elapsedSeconds.value++
            }
        }
    }

    fun stopTimer() { timerJob?.cancel() }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }

    fun loadWorkoutDetail() {
        viewModelScope.launch {
            _uiState.value = ActiveWorkoutUiState.Loading
            workoutDetailRepository.getWorkoutDetail(workoutId)
                .onSuccess { workout -> _uiState.value = ActiveWorkoutUiState.Success(workout) }
                .onFailure { error ->
                    _uiState.value = ActiveWorkoutUiState.Error(
                        error.message ?: "Failed to load workout"
                    )
                }
        }
    }

    fun addExercise(exerciseId: String) {
        viewModelScope.launch {
            val current = (_uiState.value as? ActiveWorkoutUiState.Success) ?: return@launch
            exerciseLibraryRepository.getExerciseById(exerciseId)
                .onSuccess { exercise ->
                    val newExercise = ExerciseInWorkout(
                        routineExerciseId = UUID.randomUUID().toString(),
                        externalExerciseId = exercise.id,
                        orderIndex = current.workout.exercises.size,
                        targetSets = 3,
                        targetReps = 10,
                        targetWeight = 0.0,
                        exercise = exercise
                    )
                    _uiState.value = ActiveWorkoutUiState.Success(
                        current.workout.copy(
                            exercises = current.workout.exercises + newExercise
                        )
                    )
                }
        }
    }

    fun removeExercise(routineExerciseId: String) {
        val current = (_uiState.value as? ActiveWorkoutUiState.Success) ?: return
        _uiState.value = ActiveWorkoutUiState.Success(
            current.workout.copy(
                exercises = current.workout.exercises.filter {
                    it.routineExerciseId != routineExerciseId
                }
            )
        )
    }
}