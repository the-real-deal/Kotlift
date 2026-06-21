package com.therealdeal.kotlift.data.remote

import com.therealdeal.kotlift.model.WorkoutDifficulty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO leggero usato nella lista workout.
 * Contiene solo i campi necessari per la visualizzazione in lista.
 */
@Serializable
data class WorkoutDTO(
    val name: String,
    val description: String? = null,
    val difficulty: WorkoutDifficulty? = null,
    @SerialName("estimated_time_minutes")
    val estimatedTimeMinutes: Int? = null
)