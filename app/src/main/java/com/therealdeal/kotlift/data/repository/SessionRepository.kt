package com.therealdeal.kotlift.data.repository

import com.therealdeal.kotlift.data.remote.SessionDTO
import com.therealdeal.kotlift.model.Session
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SessionRepository(
    private val supabase: SupabaseClient
) {

    suspend fun getMySessions(): Result<List<Session>> {
        return runCatching {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: error("User not authenticated")

            supabase.postgrest["sessions"]
                .select {
                    filter {
                        eq("profile_id", currentUserId)
                    }
                    order("started_at", Order.DESCENDING)
                }
                .decodeList<SessionDTO>()
                .map { it.toDomain() }
        }
    }

    suspend fun getLatestSessions(limit: Int = 3): Result<List<Session>> {
        return runCatching {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: error("User not authenticated")

            supabase.postgrest["sessions"]
                .select(Columns.raw("""
                    id,
                    profile_id,
                    workout_id,
                    started_at,
                    actual_duration_minutes,
                    total_weight_lifted,
                    workouts(name)
                    """.trimIndent())) {
                    filter {
                        eq("profile_id", currentUserId)
                    }
                    order("started_at", Order.DESCENDING)
                    limit(limit.toLong())
                }
                .decodeList<SessionDTO>()
                .map { it.toDomain() }
        }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun createSession(workoutId: String): Result<Session> {
        return runCatching {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: error("User not authenticated")

            val newSession = mapOf(
                "profile_id" to currentUserId,
                "workout_id" to workoutId,
                "started_at" to Clock.System.now().toString()
            )

            supabase.postgrest["sessions"]
                .insert(newSession) { select() }
                .decodeSingle<SessionDTO>()
                .toDomain()
        }
    }

    suspend fun completeSession(
        sessionId: String,
        actualDurationMinutes: Int,
        totalWeightLifted: Double
    ): Result<Session> {
        return runCatching {
            supabase.auth.currentUserOrNull()
                ?: error("User not authenticated")

            val updates = mapOf(
                "actual_duration_minutes" to actualDurationMinutes,
                "total_weight_lifted" to totalWeightLifted
            )

            supabase.postgrest["sessions"]
                .update(updates) {
                    filter { eq("id", sessionId) }
                    select()
                }
                .decodeSingle<SessionDTO>()
                .toDomain()
        }
    }
}