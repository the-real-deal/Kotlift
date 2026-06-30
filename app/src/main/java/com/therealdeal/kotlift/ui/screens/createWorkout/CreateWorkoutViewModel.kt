package com.therealdeal.kotlift.ui.screens.createWorkout

import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.CreateExerciseInput
import com.therealdeal.kotlift.data.repository.WorkoutRepository
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.model.Workout
import com.therealdeal.kotlift.model.WorkoutDifficulty
import com.therealdeal.kotlift.ui.baseAuthentication.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class CreateWorkoutExercise(
    val localId: String = UUID.randomUUID().toString(),
    val externalExerciseId: String,
    val name: String,
    val targetSets: Int = 3,
    val targetReps: Int = 10,
    val targetWeight: Int = 0
)

data class CreateWorkoutState(
    val name: String = "",
    val description: String = "",
    val difficulty: WorkoutDifficulty? = null,
    val estimatedTimeMinutes: String = "",
    val exercises: List<CreateWorkoutExercise> = emptyList(),
    val isNameError: Boolean = false,
    val isDifficultyError: Boolean = false,
    val isTimeError: Boolean = false,
    val isExercisesError: Boolean = false,
    val isSaving: Boolean = false,
    val savedWorkout: Workout? = null,
    val error: String? = null
)

class CreateWorkoutViewModel(
    authRepository: AuthRepository,
    private val workoutRepository: WorkoutRepository
) : BaseViewModel(authRepository) {

    private val _state = MutableStateFlow(CreateWorkoutState())
    val state: StateFlow<CreateWorkoutState> = _state.asStateFlow()

    fun onNameChange(name: String) {
        _state.update { it.copy(name = name, isNameError = name.isBlank()) }
    }

    fun onDescriptionChange(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun onDifficultyChange(difficulty: WorkoutDifficulty) {
        _state.update { it.copy(difficulty = difficulty, isDifficultyError = false) }
    }

    fun onEstimatedTimeChange(value: String) {
        _state.update { it.copy(
            estimatedTimeMinutes = value.filter { c -> c.isDigit() },
            isTimeError = value.isBlank()
        ) }
    }

    fun addExercise(externalExerciseId: String, name: String) {
        _state.update {
            it.copy(
                exercises = it.exercises + CreateWorkoutExercise(
                    externalExerciseId = externalExerciseId,
                    name = name
                ),
                isExercisesError = false
            )
        }
    }

    fun removeExercise(localId: String) {
        _state.update {
            it.copy(exercises = it.exercises.filter { ex -> ex.localId != localId })
        }
    }

    fun updateExercise(updated: CreateWorkoutExercise) {
        _state.update {
            it.copy(
                exercises = it.exercises.map { ex ->
                    if (ex.localId == updated.localId) updated else ex
                }
            )
        }
    }

    fun saveWorkout() {
        val current = _state.value

        val isNameError = current.name.isBlank()
        val isDifficultyError = current.difficulty == null
        val isTimeError = current.estimatedTimeMinutes.isBlank()
        val isExercisesError = current.exercises.isEmpty()

        if (isNameError || isDifficultyError || isTimeError || isExercisesError) {
            _state.update {
                it.copy(
                    isNameError = isNameError,
                    isDifficultyError = isDifficultyError,
                    isTimeError = isTimeError,
                    isExercisesError = isExercisesError
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            workoutRepository.createFullWorkout(
                name = current.name,
                description = current.description.ifBlank { null },
                difficulty = current.difficulty,
                estimatedTimeMinutes = current.estimatedTimeMinutes.toIntOrNull(),
                exercises = current.exercises.map { ex ->
                    CreateExerciseInput(
                        externalExerciseId = ex.externalExerciseId,
                        name = ex.name,
                        targetSets = ex.targetSets,
                        targetReps = ex.targetReps,
                        targetWeight = ex.targetWeight
                    )
                }
            )
                .onSuccess { workout ->
                    _state.update { it.copy(isSaving = false, savedWorkout = workout) }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isSaving = false, error = error.message ?: "Failed to save workout")
                    }
                }
        }
    }
}