package com.therealdeal.kotlift.model

data class ExerciseFilters(
    val targetMuscle: String? = null,
    val bodyPart: String? = null,
    val equipment: String? = null
) {
    val isActive: Boolean get() = targetMuscle != null || bodyPart != null || equipment != null
    val activeCount: Int get() = listOfNotNull(targetMuscle, bodyPart, equipment).size
}