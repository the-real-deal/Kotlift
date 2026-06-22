package com.therealdeal.kotlift.data.remote

import com.therealdeal.kotlift.model.Session
import kotlinx.datetime.Instant
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
    val totalWeightLifted: Double? = null
) {
    fun toDomain(): Session = Session(
        id = id,
        profileId = profileId,
        workoutId = workoutId,
        startedAt = startedAt,
        actualDurationMinutes = actualDurationMinutes,
        totalWeightLifted = totalWeightLifted
    )
}