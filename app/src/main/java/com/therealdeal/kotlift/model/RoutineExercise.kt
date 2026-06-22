package com.therealdeal.kotlift.model

import java.util.UUID

data class RoutineExercise(
    val id: String,
    val workoutId: String,
    val externalExerciseId: String,
    val orderIndex: Int
)