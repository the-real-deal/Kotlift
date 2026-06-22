package com.therealdeal.kotlift.model

import java.time.Instant
import java.util.UUID

data class Workout(
    val id: String,
    val createdAt: Instant = Instant.now(),
    val creatorId: String?,
    val name: String,
    val description: String?,
    val difficulty: WorkoutDifficulty = WorkoutDifficulty.Beginner,
    val estimatedTimeMinutes: Int?
)