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

    suspend fun getMyWorkouts(): Result<List<Workout>> {
        return runCatching {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: error("User not authenticated")

            supabase.postgrest["workouts"]
                .select(
                    columns = Columns.list(
                        "id",
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
                .map { it.toDomain() }
        }
    }

    fun getMyWorkoutsFlow(): Flow<Result<List<Workout>>> = flow {
        emit(getMyWorkouts())
    }

    suspend fun getWorkoutById(workoutId: String): Result<Workout> {
        return runCatching {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: error("User not authenticated")

            supabase.postgrest["workouts"]
                .select {
                    filter {
                        eq("id", workoutId)
                        eq("creator_id", currentUserId)
                    }
                    limit(1)
                }
                .decodeSingle<WorkoutDTO>()
                .toDomain()
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
                ?: error("User not authenticated")

            val newWorkout = mapOf(
                "creator_id" to currentUserId,
                "name" to name,
                "description" to description,
                "difficulty" to difficulty,
                "estimated_time_minutes" to estimatedTimeMinutes
            ).filterValues { it != null }

            supabase.postgrest["workouts"]
                .insert(newWorkout) { select() }
                .decodeSingle<WorkoutDTO>()
                .toDomain()
        }
    }

    suspend fun updateWorkout(
        workoutId: String,
        name: String? = null,
        description: String? = null,
        difficulty: String? = null,
        estimatedTimeMinutes: Int? = null
    ): Result<Workout> {
        return runCatching {
            supabase.auth.currentUserOrNull()
                ?: error("User not authenticated")

            val updates = buildMap {
                name?.let { put("name", it) }
                description?.let { put("description", it) }
                difficulty?.let { put("difficulty", it) }
                estimatedTimeMinutes?.let { put("estimated_time_minutes", it) }
            }

            check(updates.isNotEmpty()) { "No fields to update" }

            supabase.postgrest["workouts"]
                .update(updates) {
                    filter { eq("id", workoutId) }
                    select()
                }
                .decodeSingle<WorkoutDTO>()
                .toDomain()
        }
    }

    suspend fun deleteWorkout(workoutId: String): Result<Unit> {
        return runCatching {
            supabase.auth.currentUserOrNull()
                ?: error("User not authenticated")

            supabase.postgrest["workouts"]
                .delete {
                    filter { eq("id", workoutId) }
                }
        }
    }
}