package com.therealdeal.kotlift.model

data class ExerciseWithDetails(
    val routineExerciseId: String,
    val externalExerciseId: String,
    val orderIndex: Int,
    val sets: List<RoutineSet>,
    val exercise: Exercise
)
