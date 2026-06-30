package com.therealdeal.kotlift.data.repository

import com.therealdeal.kotlift.data.remote.WorkoutDTO
import com.therealdeal.kotlift.model.Workout
import com.therealdeal.kotlift.model.WorkoutDifficulty
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class CreateWorkoutRequest(
    @SerialName("creator_id") val creatorId: String,
    val name: String,
    val description: String?,
    val difficulty: String?,
    @SerialName("estimated_time_minutes") val estimatedTimeMinutes: Int?
)

@Serializable
private data class CreateRoutineExerciseRequest(
    @SerialName("workout_id") val workoutId: String,
    @SerialName("external_exercise_id") val externalExerciseId: String,
    @SerialName("order_index") val orderIndex: Int,
    @SerialName("target_sets") val targetSets: Int,
    @SerialName("target_reps") val targetReps: Int,
    @SerialName("target_weight") val targetWeight: Int
)

data class CreateExerciseInput(
    val externalExerciseId: String,
    val name: String,
    val targetSets: Int,
    val targetReps: Int,
    val targetWeight: Int
)

class WorkoutRepository(
    private val supabase: SupabaseClient
) {
    private val _invalidate = MutableSharedFlow<Unit>(replay = 1).also { it.tryEmit(Unit) }

    fun invalidate() {
        _invalidate.tryEmit(Unit)
    }

    fun getMyWorkoutsFlow(): Flow<Result<List<Workout>>> =
        _invalidate.flatMapLatest {
            flow { emit(getMyWorkouts()) }
        }

    suspend fun getMyWorkouts(): Result<List<Workout>> {
        return runCatching {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: error("User not authenticated")

            supabase.postgrest["workouts"]
                .select(
                    columns = Columns.list(
                        "id", "name", "description", "difficulty", "estimated_time_minutes"
                    )
                ) {
                    filter { eq("creator_id", currentUserId) }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<WorkoutDTO>()
                .map { it.toDomain() }
        }
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

    suspend fun createFullWorkout(
        name: String,
        description: String?,
        difficulty: WorkoutDifficulty?,
        estimatedTimeMinutes: Int?,
        exercises: List<CreateExerciseInput>
    ): Result<Workout> {
        return runCatching {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: error("User not authenticated")

            val workout = supabase.postgrest["workouts"]
                .insert(
                    CreateWorkoutRequest(
                        creatorId = currentUserId,
                        name = name,
                        description = description,
                        difficulty = difficulty?.name,
                        estimatedTimeMinutes = estimatedTimeMinutes
                    )
                ) { select() }
                .decodeSingle<WorkoutDTO>()
                .toDomain()

            if (exercises.isNotEmpty()) {
                supabase.postgrest["routine_exercises"].insert(
                    exercises.mapIndexed { index, ex ->
                        CreateRoutineExerciseRequest(
                            workoutId = workout.id,
                            externalExerciseId = ex.externalExerciseId,
                            orderIndex = index,
                            targetSets = ex.targetSets,
                            targetReps = ex.targetReps,
                            targetWeight = ex.targetWeight
                        )
                    }
                )
            }

            invalidate()
            workout
        }
    }

    suspend fun deleteWorkout(workoutId: String): Result<Unit> {
        return runCatching {
            supabase.auth.currentUserOrNull() ?: error("User not authenticated")

            supabase.postgrest["workouts"]
                .delete {
                    filter { eq("id", workoutId) }
                }

            invalidate()
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
            supabase.auth.currentUserOrNull() ?: error("User not authenticated")

            val updates = buildMap<String, Any> {
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
}