package com.therealdeal.kotlift.data.repository

import com.therealdeal.kotlift.data.remote.AchievementDTO
import com.therealdeal.kotlift.data.remote.EarnedAchievementDTO
import com.therealdeal.kotlift.model.Achievement
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class AchievementsRepository(
    private val client: SupabaseClient
) {
    suspend fun getAchievementsForUser(
        profileId: String
    ): List<Achievement> {

    val allAchievements = client.postgrest["achievements"]
        .select()
        .decodeList<AchievementDTO>()

    val earnedAchievements = client.postgrest["earned_achievements"]
        .select {
            filter {
                eq("profile_id", profileId)
            }
        }
        .decodeList<EarnedAchievementDTO>()

    val earnedMap = earnedAchievements.associateBy { it.achievementId }

    return allAchievements.map { achievement ->
            val earned = earnedMap[achievement.id]
            Achievement(
                id = achievement.id,
                name = achievement.name,
                description = achievement.description,
                functionName = achievement.functionName,
                isCompleted = earned != null,
                completedAt = earned?.unlockedAt,
                threshold = achievement.threshold
            )
        }
    }

    suspend fun loadProgress(
        achievement: Achievement,
        profileId: String,
    ) : Int {
        val response = client.postgrest.rpc(
            function = achievement.functionName,
            parameters = mapOf("p_profile_id" to profileId)
        )
        return response.decodeAs<Int>()
    }
}
