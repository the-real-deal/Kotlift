package com.therealdeal.kotlift.model

import java.util.UUID

data class SessionExercise(
    val id: UUID = UUID.randomUUID(),
    val sessionId: UUID,
    val externalExerciseId: String,
    val orderIndex: Int
)