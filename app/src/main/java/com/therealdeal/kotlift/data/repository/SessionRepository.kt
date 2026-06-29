package com.therealdeal.kotlift.data.repository

import com.therealdeal.kotlift.data.remote.SessionDTO
import com.therealdeal.kotlift.data.remote.SessionExerciseDTO
import com.therealdeal.kotlift.model.ExerciseInWorkout
import com.therealdeal.kotlift.model.Session
import com.therealdeal.kotlift.ui.composables.cards.SetData
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.serialization.Serializable

@Serializable
private data class CreateSessionRequest(
    @SerialName("profile_id") val profileId: String,
    @SerialName("workout_id") val workoutId: String,
    @SerialName("started_at") val startedAt: String
)

@Serializable
private data class CompleteSessionRequest(
    @SerialName("actual_duration_minutes") val actualDurationMinutes: Int,
    @SerialName("total_weight_lifted") val totalWeightLifted: Double
)

@Serializable
private data class CreateSessionExerciseRequest(
    @SerialName("session_id") val sessionId: String,
    @SerialName("external_exercise_id") val externalExerciseId: String,
    @SerialName("order_index") val orderIndex: Int
)

@Serializable
private data class CreateSessionSetRequest(
    @SerialName("session_exercise_id") val sessionExerciseId: String,
    @SerialName("set_order") val setOrder: Int,
    @SerialName("performed_reps") val performedReps: Int,
    val weight: Double
)

class SessionRepository(
    private val supabase: SupabaseClient
) {

    suspend fun getMySessions(): Result<List<Session>> {
        return runCatching {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: error("User not authenticated")

            supabase.postgrest["sessions"]
                .select {
                    filter { eq("profile_id", currentUserId) }
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
                    filter { eq("profile_id", currentUserId) }
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

            supabase.postgrest["sessions"]
                .insert(
                    CreateSessionRequest(
                        profileId = currentUserId,
                        workoutId = workoutId,
                        startedAt = Clock.System.now().toString()
                    )
                ) { select() }
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
            supabase.auth.currentUserOrNull() ?: error("User not authenticated")

            supabase.postgrest["sessions"]
                .update(
                    CompleteSessionRequest(
                        actualDurationMinutes = actualDurationMinutes,
                        totalWeightLifted = totalWeightLifted
                    )
                ) {
                    filter { eq("id", sessionId) }
                    select()
                }
                .decodeSingle<SessionDTO>()
                .toDomain()
        }
    }

    suspend fun saveSessionExercisesAndSets(
        sessionId: String,
        exercises: List<ExerciseInWorkout>,
        setsMap: Map<String, List<SetData>>
    ): Result<Unit> {
        return runCatching {
            supabase.auth.currentUserOrNull() ?: error("User not authenticated")

            exercises.forEachIndexed { index, exercise ->
                val sessionExercise = supabase.postgrest["session_exercises"]
                    .insert(
                        CreateSessionExerciseRequest(
                            sessionId = sessionId,
                            externalExerciseId = exercise.externalExerciseId,
                            orderIndex = index
                        )
                    ) { select() }
                    .decodeSingle<SessionExerciseDTO>()

                val sets = setsMap[exercise.routineExerciseId] ?: return@forEachIndexed
                val doneSets = sets.filter { it.isDone }
                if (doneSets.isEmpty()) return@forEachIndexed

                val setInserts = doneSets.mapIndexed { setIndex, set ->
                    CreateSessionSetRequest(
                        sessionExerciseId = sessionExercise.id,
                        setOrder = setIndex,
                        performedReps = set.reps.ifBlank { set.targetReps.toString() }.toIntOrNull() ?: 0,
                        weight = set.weight.ifBlank { set.suggestedWeight }.toDoubleOrNull() ?: 0.0
                    )
                }

                supabase.postgrest["session_sets"].insert(setInserts)
            }
        }
    }

    suspend fun deleteSession(sessionId: String): Result<Unit> {
        return runCatching {
            supabase.auth.currentUserOrNull() ?: error("User not authenticated")
            supabase.postgrest["sessions"]
                .delete {
                    filter { eq("id", sessionId) }
                }
        }
    }
}