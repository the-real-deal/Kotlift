package com.therealdeal.kotlift.model

import java.util.UUID

data class RoutineSet(
    val id: Int = 0,
    val routineExerciseId: String,
    val setNumber: Int,
    val targetReps: Int
)