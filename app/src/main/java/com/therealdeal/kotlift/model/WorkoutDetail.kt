package com.therealdeal.kotlift.model

import kotlin.time.Instant

data class WorkoutDetail(
    val id: String,
    val name: String,
    val description: String?,
    val difficulty: WorkoutDifficulty?,
    val estimatedTimeMinutes: Int?,
    val createdAt: Instant,
    val exercises: List<ExerciseInWorkout>
)

