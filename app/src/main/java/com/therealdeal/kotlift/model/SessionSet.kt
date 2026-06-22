package com.therealdeal.kotlift.model

import java.util.UUID

data class SessionSet(
    val id: Int = 0,
    val sessionExerciseId: String,
    val setNumber: Int,
    val performedReps: Int,
    val weight: Double
)