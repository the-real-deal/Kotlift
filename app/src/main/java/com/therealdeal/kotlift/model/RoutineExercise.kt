package com.therealdeal.kotlift.model

data class RoutineExercise(
    val id: String,
    val workoutId: String,
    val externalExerciseId: String,
    val orderIndex: Int,
    val targetSets: Int,
    val targetReps: Int,
    val targetWeight: Double
)