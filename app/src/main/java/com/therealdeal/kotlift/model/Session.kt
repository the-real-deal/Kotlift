package com.therealdeal.kotlift.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Session(
    val id: String,
    @SerialName("profile_id")
    val profileId: String,
    @SerialName("workout_id")
    val workoutId: String,
    @SerialName("started_at")
    val startedAt: Instant?,
    @SerialName("actual_duration_minutes")
    val actualDurationMinutes: Int? = null,
    @SerialName("total_weight_lifted")
    val totalWeightLifted: Double? = null
)