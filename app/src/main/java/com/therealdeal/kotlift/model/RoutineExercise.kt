package com.therealdeal.kotlift.model

import java.util.UUID

data class RoutineExercise(
    val id: UUID = UUID.randomUUID(),
    val workoutId: UUID,
    val externalExerciseId: String,
    val orderIndex: Int
)