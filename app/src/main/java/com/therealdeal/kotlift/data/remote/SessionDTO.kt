package com.therealdeal.kotlift.data.remote

import com.therealdeal.kotlift.model.Session
import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionDTO(
    val id: String,
    @SerialName("profile_id")
    val profileId: String,
    @SerialName("workout_id")
    val workoutId: String,
    @SerialName("started_at")
    val startedAt: Instant,
    @SerialName("actual_duration_minutes")
    val actualDurationMinutes: Int? = null,
    @SerialName("total_weight_lifted")
    val totalWeightLifted: Double? = null,
    @SerialName("workouts")
    val workout: WorkoutTitleDTO? = null
) {
        fun toDomain(): Session = Session(
        id = id,
        profileId = profileId,
        workoutId = workoutId,
        startedAt = startedAt,
        actualDurationMinutes = actualDurationMinutes,
        totalWeightLifted = totalWeightLifted,
        workoutTitle = workout?.name ?: "Unknown Workout"
    )
}

@Serializable
data class WorkoutTitleDTO(
    val name: String
)

@Serializable
data class CreateSessionRequestDTO(
    @SerialName("profile_id") val profileId: String,
    @SerialName("workout_id") val workoutId: String,
    @SerialName("started_at") val startedAt: String,
    @SerialName("actual_duration_minutes") val actualDurationMinutes: Int,
    @SerialName("total_weight_lifted") val totalWeightLifted: Double
)

@Serializable
data class CreateSessionExerciseRequestDTO(
    @SerialName("session_id") val sessionId: String,
    @SerialName("external_exercise_id") val externalExerciseId: String,
    @SerialName("order_index") val orderIndex: Int
)

@Serializable
data class CreateSessionSetRequestDTO(
    @SerialName("session_exercise_id") val sessionExerciseId: String,
    @SerialName("set_order") val setOrder: Int,
    @SerialName("performed_reps") val performedReps: Int,
    val weight: Double
)
