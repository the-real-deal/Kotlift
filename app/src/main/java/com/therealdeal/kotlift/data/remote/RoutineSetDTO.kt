package com.therealdeal.kotlift.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoutineSetDTO(
    val id: Int,
    @SerialName("routine_exercise_id")
    val routineExerciseId: String,
    @SerialName("set_number")
    val setNumber: Int,
    @SerialName("target_reps")
    val targetReps: Int
)