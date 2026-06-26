package com.therealdeal.kotlift.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoutineExerciseDTO(
    val id: String,
    @SerialName("workout_id")
    val workoutId: String,
    @SerialName("external_exercise_id")
    val externalExerciseId: String,
    @SerialName("order_index")
    val orderIndex: Int,
    @SerialName("target_sets")
    val targetSets: Int,
    @SerialName("target_reps")
    val targetReps: Int,
    @SerialName("target_weight")
    val targetWeight: Double
)