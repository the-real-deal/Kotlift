package com.therealdeal.kotlift.model

data class ExerciseInWorkout(
    val routineExerciseId: String,
    val externalExerciseId: String,
    val orderIndex: Int,
    val targetSets: Int,
    val targetReps: Int,
    val targetWeight: Double,
    val exercise: Exercise
)