package com.therealdeal.kotlift.data.remote
import com.therealdeal.kotlift.model.GeneralExercise
import kotlinx.serialization.Serializable

@Serializable
data class GeneralExerciseDTO(
    val exerciseId: String,
    val name: String,
    val gifUrl: String
) {
    fun toDomain(): GeneralExercise = GeneralExercise(
        id = exerciseId,
        name = name,
        gifUrl = gifUrl
    )
}