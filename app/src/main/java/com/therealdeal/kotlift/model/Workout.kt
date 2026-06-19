package com.therealdeal.kotlift.model

import java.time.Instant
import java.util.UUID

data class Workout(
    val id: UUID = UUID.randomUUID(),
    val createdAt: Instant = Instant.now(),
    val creatorId: UUID?,
    val name: String,
    val description: String?,
    val difficulty: WorkoutDifficulty = WorkoutDifficulty.Beginner,
    val estimatedTimeMinutes: Int?
)