package com.therealdeal.kotlift.data.remote

import com.therealdeal.kotlift.model.WorkoutDifficulty
import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkoutDetailDTO(
    val id: String,
    val name: String,
    val description: String? = null,
    val difficulty: WorkoutDifficulty? = null,
    @SerialName("estimated_time_minutes")
    val estimatedTimeMinutes: Int? = null,
    @SerialName("created_at")
    val createdAt: Instant
)