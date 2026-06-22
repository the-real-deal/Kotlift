package com.therealdeal.kotlift.model

data class Exercise(
    val id: String,
    val name: String,
    val gifUrl: String,
    val bodyParts: List<String> = emptyList(),
    val targetMuscles: List<String> = emptyList(),
    val equipment: List<String> = emptyList()
)