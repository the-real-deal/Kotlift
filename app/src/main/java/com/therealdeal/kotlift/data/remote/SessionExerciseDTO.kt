package com.therealdeal.kotlift.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionExerciseDTO(
    val id: String,
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("external_exercise_id")
    val externalExerciseId: String,
    @SerialName("order_index")
    val orderIndex: Int
)

@Serializable
data class SessionSetDTO(
    val id: Int? = null,
    @SerialName("session_exercise_id")
    val sessionExerciseId: String,
    @SerialName("set_order")
    val setOrder: Int,
    @SerialName("performed_reps")
    val performedReps: Int,
    val weight: Double
)