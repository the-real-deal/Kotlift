package com.therealdeal.kotlift.data.repository

import com.therealdeal.kotlift.data.remote.AchievementDTO
import com.therealdeal.kotlift.data.remote.EarnedAchievementDTO
import com.therealdeal.kotlift.data.remote.ProfileStatsUpdate
import com.therealdeal.kotlift.data.remote.ProfileStatsUpdateNoStreak
import com.therealdeal.kotlift.data.remote.SessionStartedAt
import com.therealdeal.kotlift.model.Achievement
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Instant

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

    suspend fun checkAllProgress(
        profileId: String
    ) {
        client.postgrest.rpc(
            function = "check_profile_achievements",
            parameters = mapOf("p_profile_id" to profileId)
        )
    }

    /**
     * Recomputes the denormalized stat columns on `profiles`
     * (total_sessions, day_streak, unlocked_achievements_count)
     * from the source-of-truth tables and writes them back.
     */
    suspend fun updateStats(profileId: String) {

        val totalSessions = client.postgrest["sessions"]
            .select(columns = Columns.list("id")) {
                filter { eq("profile_id", profileId) }
                count(Count.EXACT)
            }
            .countOrNull()?.toInt() ?: 0

        val unlockedAchievements = client.postgrest["earned_achievements"]
            .select(columns = Columns.list("achievement_id")) {
                filter { eq("profile_id", profileId) }
                count(Count.EXACT)
            }
            .countOrNull()?.toInt() ?: 0

        val lastDate = client.postgrest["sessions"]
            .select(columns = Columns.list("started_at")) {
                filter { eq("profile_id", profileId) }
                order("started_at", Order.DESCENDING)
                limit(1) // small buffer in case of multiple sessions on the same day
            }
            .decodeSingle<SessionStartedAt>()

        if (checkStreakContinue(Instant.parse(lastDate.startedAt).toLocalDateTime(TimeZone.currentSystemDefault()).date)) {
            client.postgrest["profiles"].update(
                ProfileStatsUpdateNoStreak(
                    totalSessions = totalSessions,
                    unlockedAchievementsCount = unlockedAchievements
                )
            ) {
                filter { eq("id", profileId) }
            }
        } else {
            client.postgrest["profiles"].update(
                ProfileStatsUpdate(
                    totalSessions = totalSessions,
                    dayStreak = 0,
                    unlockedAchievementsCount = unlockedAchievements
                )
            ) {
                filter { eq("id", profileId) }
            }
        }
    }

    private fun checkStreakContinue(lastDate: LocalDate): Boolean {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val yesterday = today.minus(DatePeriod(days = 1))

        return !(lastDate != today && lastDate != yesterday)
    }


}
