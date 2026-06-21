package com.therealdeal.kotlift.data.repository

import com.therealdeal.kotlift.data.remote.WorkoutDTO
import com.therealdeal.kotlift.model.Workout
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WorkoutRepository(
    private val supabase: SupabaseClient
) {

    suspend fun getMyWorkouts(): Result<List<WorkoutDTO>> {
        return runCatching {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: error("User must be authenticated")

            supabase.postgrest["workouts"]
                .select(
                    columns = Columns.list(
                        "name",
                        "description",
                        "difficulty",
                        "estimated_time_minutes"
                    )
                ) {
                    filter {
                        eq("creator_id", currentUserId)
                    }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<WorkoutDTO>()
        }
    }

    /**
     * Versione Flow di getMyWorkouts — utile per osservare aggiornamenti
     * in tempo reale nella UI (es. con collectAsStateWithLifecycle).
     */
    fun getMyWorkoutsFlow(): Flow<Result<List<WorkoutDTO>>> = flow {
        emit(getMyWorkouts())
    }

    suspend fun getWorkoutById(workoutId: String): Result<Workout> {
        return runCatching {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: error("User must be authenticated")

            supabase.postgrest["workouts"]
                .select {
                    filter {
                        eq("id", workoutId)
                        eq("creator_id", currentUserId)
                    }
                    limit(1)
                }
                .decodeSingle<Workout>()
        }
    }

    suspend fun createWorkout(
        name: String,
        description: String? = null,
        difficulty: String? = null,
        estimatedTimeMinutes: Int? = null
    ): Result<Workout> {
        return runCatching {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: error("User must be authenticated")

            val newWorkout = mapOf(
                "creator_id" to currentUserId,
                "name" to name,
                "description" to description,
                "difficulty" to difficulty,
                "estimated_time_minutes" to estimatedTimeMinutes
            ).filterValues { it != null }

            supabase.postgrest["workouts"]
                .insert(newWorkout) {
                    select()
                }
                .decodeSingle<Workout>()
        }
    }

    suspend fun deleteWorkout(workoutId: String): Result<Unit> {
        return runCatching {
            supabase.auth.currentUserOrNull()
                ?: error("User must be authenticated")

            supabase.postgrest["workouts"]
                .delete {
                    filter { eq("id", workoutId) }
                }
        }
    }
}