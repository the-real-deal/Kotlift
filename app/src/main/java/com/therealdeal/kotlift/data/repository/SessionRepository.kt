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
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
private data class CreateSessionRequest(
    @SerialName("profile_id") val profileId: String,
    @SerialName("workout_id") val workoutId: String,
    @SerialName("started_at") val startedAt: String,
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
    suspend fun saveFullSession(
        workoutId: String,
        durationMinutes: Int,
        totalWeightLifted: Double,
        exercises: List<ExerciseInWorkout>,
        setsMap: Map<String, List<SetData>>
    ): Result<Unit> {
        return runCatching {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: error("User not authenticated")

            // 1. Crea la sessione completa in un colpo solo
            val session = supabase.postgrest["sessions"]
                .insert(
                    CreateSessionRequest(
                        profileId = currentUserId,
                        workoutId = workoutId,
                        startedAt = Clock.System.now().toString(),
                        actualDurationMinutes = durationMinutes,
                        totalWeightLifted = totalWeightLifted
                    )
                ) { select() }
                .decodeSingle<SessionDTO>()

            // 2. Per ogni esercizio, crea session_exercise e i relativi set
            exercises.forEachIndexed { index, exercise ->
                val sessionExercise = supabase.postgrest["session_exercises"]
                    .insert(
                        CreateSessionExerciseRequest(
                            sessionId = session.id,
                            externalExerciseId = exercise.externalExerciseId,
                            orderIndex = index
                        )
                    ) { select() }
                    .decodeSingle<SessionExerciseDTO>()

                val sets = setsMap[exercise.routineExerciseId] ?: return@forEachIndexed
                val doneSets = sets.filter { it.isDone }
                if (doneSets.isEmpty()) return@forEachIndexed

                supabase.postgrest["session_sets"].insert(
                    doneSets.mapIndexed { setIndex, set ->
                        CreateSessionSetRequest(
                            sessionExerciseId = sessionExercise.id,
                            setOrder = setIndex,
                            performedReps = set.reps.ifBlank { set.targetReps.toString() }.toIntOrNull() ?: 0,
                            weight = set.weight.ifBlank { set.suggestedWeight }.toDoubleOrNull() ?: 0.0
                        )
                    }
                )
            }
        }
    }
}