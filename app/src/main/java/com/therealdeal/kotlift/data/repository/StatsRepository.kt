package com.therealdeal.kotlift.data.repository

import com.therealdeal.kotlift.data.remote.SessionDTO
import com.therealdeal.kotlift.model.Session
import com.therealdeal.kotlift.model.Stats
import com.therealdeal.kotlift.model.WorkoutFrequency
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class StatsRepository(
    private val supabase: SupabaseClient
) {

    @OptIn(ExperimentalTime::class)
    suspend fun getStats(): Result<Stats> {
        return runCatching {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: error("User not authenticated")

            val sessions = supabase.postgrest["sessions"]
                .select(Columns.raw("""
                    id,
                    profile_id,
                    workout_id,
                    started_at,
                    actual_duration_minutes,
                    total_weight_lifted,
                    workouts(name)
                """.trimIndent())) {
                    filter { eq("profile_id", currentUserId) }
                    order("started_at", Order.DESCENDING)
                }
                .decodeList<SessionDTO>()
                .map { it.toDomain() }

            computeStats(sessions)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun computeStats(sessions: List<Session>): Stats {
        val now = Clock.System.now()
        val zone = TimeZone.currentSystemDefault()
        val today = now.toLocalDateTime(zone).date

        val totalWeightLifted = sessions.sumOf { it.totalWeightLifted ?: 0.0 }
        val totalSessions = sessions.size
        val avgSessionMinutes = sessions.mapNotNull { it.actualDurationMinutes }
            .average().takeIf { !it.isNaN() } ?: 0.0

        val last7Days = (0..6).map { daysAgo ->
            val day = today.minus(daysAgo, DateTimeUnit.DAY)
            val hoursForDay = sessions
                .filter { it.startedAt?.toLocalDateTime(zone)?.date == day }
                .sumOf { it.actualDurationMinutes ?: 0 } / 60.0
            day to hoursForDay
        }.reversed()

        val last30DaysStart = now.minus(30, DateTimeUnit.DAY, zone)
        val mostDoneWorkout = sessions
            .filter { it.startedAt != null && it.startedAt > last30DaysStart }
            .groupBy { it.workoutId }
            .maxByOrNull { it.value.size }
            ?.let { WorkoutFrequency(workoutId = it.key, count = it.value.size) }

        return Stats(
            totalWeightLifted = totalWeightLifted,
            totalSessions = totalSessions,
            avgSessionMinutes = avgSessionMinutes,
            last7DaysActivity = last7Days,
            mostDoneWorkout = mostDoneWorkout,
            allSessions = sessions
        )
    }
}