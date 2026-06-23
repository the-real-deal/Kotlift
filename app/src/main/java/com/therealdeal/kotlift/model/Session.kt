package com.therealdeal.kotlift.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

data class Session(
    val id: String,
    val workoutTitle: String,
    val profileId: String,
    val workoutId: String,
    val startedAt: Instant?,
    val actualDurationMinutes: Int? = null,
    val totalWeightLifted: Double? = null
)