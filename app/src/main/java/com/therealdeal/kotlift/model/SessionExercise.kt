package com.therealdeal.kotlift.model

import java.util.UUID

data class SessionExercise(
    val id: String,
    val sessionId: String,
    val externalExerciseId: String,
    val orderIndex: Int
)