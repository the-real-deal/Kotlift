package com.therealdeal.kotlift.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class EarnedAchievementDTO(
    @SerialName("profile_id") val profileId: String,
    @SerialName("achievement_id") val achievementId: String,
    @SerialName("unlocked_at") val unlockedAt: Instant
)

@Serializable
data class AchievementDTO(
    val id: String,
    val name: String,
    val description: String,
    @SerialName("function_name") val functionName: String,
    val threshold: Int
)
