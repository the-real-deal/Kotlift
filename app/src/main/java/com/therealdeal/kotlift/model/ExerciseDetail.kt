package com.therealdeal.kotlift.model

import kotlin.time.Instant

data class ExerciseDetail (
    val exerciseId: String,
    val name: String,
    val gifUrl: String?,
    val targetMuscles: List<String> = emptyList(),
    val bodyParts: List<String> = emptyList(),
    val equipments: List<String> = emptyList(),
    val secondaryMuscles: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
)