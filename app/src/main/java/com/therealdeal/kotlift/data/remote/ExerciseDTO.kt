package com.therealdeal.kotlift.data.remote

import com.therealdeal.kotlift.model.Exercise
import com.therealdeal.kotlift.model.ExerciseDetail
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseDTO(
    val exerciseId: String,
    val name: String,
    val gifUrl: String,
    val bodyParts: List<String> = emptyList(),
    val equipments: List<String> = emptyList(),
    val targetMuscles: List<String> = emptyList(),
    val secondaryMuscles: List<String> = emptyList(),
    val instructions: List<String> = emptyList()
) {
    fun toDomain(): Exercise = Exercise(
        id = exerciseId,
        name = name,
        gifUrl = gifUrl,
        bodyParts = bodyParts,
        targetMuscles = targetMuscles,
        equipment = equipments
    )

    fun toDomainWithDetail(): ExerciseDetail = ExerciseDetail(
        exerciseId = exerciseId,
        name = name,
        gifUrl = gifUrl,
        targetMuscles = bodyParts,
        bodyParts = equipments,
        equipments = targetMuscles,
        secondaryMuscles = secondaryMuscles,
        instructions = instructions
    )
}