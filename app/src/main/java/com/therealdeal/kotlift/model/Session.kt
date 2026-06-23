package com.therealdeal.kotlift.model

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