package com.therealdeal.kotlift.data.remote

import com.therealdeal.kotlift.model.Workout
import com.therealdeal.kotlift.model.WorkoutDifficulty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class WorkoutDTO(
    val id: String,
    val name: String,
    val description: String? = null,
    val difficulty: WorkoutDifficulty? = null,
    @SerialName("estimated_time_minutes")
    val estimatedTimeMinutes: Int? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("creator_id")
    val creatorId: String? = null
) {
    fun toDomain(): Workout = Workout(
        id = id,
        createdAt = createdAt?.let { Instant.parse(it) } ?: Instant.now(),
        creatorId = creatorId,
        name = name,
        description = description,
        difficulty = difficulty ?: WorkoutDifficulty.Beginner,
        estimatedTimeMinutes = estimatedTimeMinutes
    )
}