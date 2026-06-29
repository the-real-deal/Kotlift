package com.therealdeal.kotlift.ui.screens.activeWorkout

import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.data.repository.ExerciseLibraryRepository
import com.therealdeal.kotlift.data.repository.SessionRepository
import com.therealdeal.kotlift.data.repository.WorkoutDetailRepository
import com.therealdeal.kotlift.model.ExerciseInWorkout
import com.therealdeal.kotlift.ui.baseAuthentication.BaseViewModel
import com.therealdeal.kotlift.ui.composables.cards.SetData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface ActiveWorkoutUiState {
    data object Loading : ActiveWorkoutUiState
    data class Success(val workout: com.therealdeal.kotlift.model.WorkoutDetail) : ActiveWorkoutUiState
    data class Error(val message: String) : ActiveWorkoutUiState
}

sealed interface SaveSessionState {
    data object Idle : SaveSessionState
    data object Saving : SaveSessionState
    data object Saved : SaveSessionState
    data class Error(val message: String) : SaveSessionState
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

    private val _saveSessionState = MutableStateFlow<SaveSessionState>(SaveSessionState.Idle)
    val saveSessionState: StateFlow<SaveSessionState> = _saveSessionState.asStateFlow()

    private var timerJob: Job? = null
    private var currentSessionId: String? = null

    init {
        loadWorkoutDetail()
        startTimer()
        startSession()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _elapsedSeconds.value++
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }

    fun loadWorkoutDetail() {
        viewModelScope.launch {
            _uiState.value = ActiveWorkoutUiState.Loading
            workoutDetailRepository.getWorkoutDetail(workoutId)
                .onSuccess { _uiState.value = ActiveWorkoutUiState.Success(it) }
                .onFailure { _uiState.value = ActiveWorkoutUiState.Error(it.message ?: "Failed to load workout") }
        }
    }

    private fun startSession() {
        viewModelScope.launch {
            sessionRepository.createSession(workoutId)
                .onSuccess { currentSessionId = it.id }
        }
    }

    fun finishWorkout(setsMap: Map<String, List<SetData>>) {
        val sessionId = currentSessionId ?: return
        val current = (_uiState.value as? ActiveWorkoutUiState.Success) ?: return
        val durationMinutes = (_elapsedSeconds.value / 60).toInt()

        val totalWeight = setsMap.values.flatten()
            .filter { it.isDone }
            .sumOf { set ->
                val weight = set.weight.ifBlank { set.suggestedWeight }.toDoubleOrNull() ?: 0.0
                val reps = set.reps.ifBlank { set.targetReps.toString() }.toIntOrNull() ?: 0
                weight * reps
            }

        viewModelScope.launch {
            _saveSessionState.value = SaveSessionState.Saving

            sessionRepository.saveSessionExercisesAndSets(
                sessionId = sessionId,
                exercises = current.workout.exercises,
                setsMap = setsMap
            ).onFailure {
                _saveSessionState.value = SaveSessionState.Error(it.message ?: "Failed to save sets")
                return@launch
            }

            sessionRepository.completeSession(
                sessionId = sessionId,
                actualDurationMinutes = durationMinutes,
                totalWeightLifted = totalWeight
            )
                .onSuccess { _saveSessionState.value = SaveSessionState.Saved }
                .onFailure { _saveSessionState.value = SaveSessionState.Error(it.message ?: "Failed to save session") }
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
                        current.workout.copy(exercises = current.workout.exercises + newExercise)
                    )
                }
        }
    }

    fun removeExercise(routineExerciseId: String) {
        val current = (_uiState.value as? ActiveWorkoutUiState.Success) ?: return
        _uiState.value = ActiveWorkoutUiState.Success(
            current.workout.copy(
                exercises = current.workout.exercises.filter { it.routineExerciseId != routineExerciseId }
            )
        )
    }

    fun abandonWorkout() {
        val sessionId = currentSessionId ?: return
        stopTimer()
        viewModelScope.launch {
            sessionRepository.deleteSession(sessionId)
        }
    }
}