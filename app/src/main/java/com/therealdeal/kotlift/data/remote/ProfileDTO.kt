package com.therealdeal.kotlift.data.remote

import com.therealdeal.kotlift.model.Profile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDTO(
    val id: String,
    @SerialName("updated_at") val updatedAt: String?,
    @SerialName("profile_picture") val profilePicture: String?,
    @SerialName("day_streak") val dayStreak: Int,
    @SerialName("total_sessions") val totalSessions: Int,
    @SerialName("unlocked_achievements_count") val unlockedAchievementsCount: Int
) {
    fun toDomain(
        username: String?,
        email: String?
    ) = Profile(
        id = id,
        username = username,
        email = email,
        dayStreak = dayStreak,
        totalSessions = totalSessions,
        unlockedAchievementsCount = unlockedAchievementsCount
    )
}
