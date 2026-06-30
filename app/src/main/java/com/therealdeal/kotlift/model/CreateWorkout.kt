package com.therealdeal.kotlift.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class CreateWorkoutRequest(
    @SerialName("creator_id") val creatorId: String,
    val name: String,
    val description: String?,
    val difficulty: String?,
    @SerialName("estimated_time_minutes") val estimatedTimeMinutes: Int?
)

@Serializable
private data class CreateRoutineExerciseRequest(
    @SerialName("workout_id") val workoutId: String,
    @SerialName("external_exercise_id") val externalExerciseId: String,
    @SerialName("order_index") val orderIndex: Int,
    @SerialName("target_sets") val targetSets: Int,
    @SerialName("target_reps") val targetReps: Int,
    @SerialName("target_weight") val targetWeight: Int
)

data class CreateExerciseInput(
    val externalExerciseId: String,
    val name: String,
    val targetSets: Int,
    val targetReps: Int,
    val targetWeight: Int
)