package com.therealdeal.kotlift.model

import java.time.Instant
import java.util.UUID

data class Session(
    val id: UUID = UUID.randomUUID(),
    val profileId: UUID,
    val workoutId: UUID?,
    val startedAt: Instant = Instant.now(),
    val actualDurationMinutes: Int?,
    val totalWeightLifted: Double = 0.0
)