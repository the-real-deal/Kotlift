package com.therealdeal.kotlift.data.repository

import com.therealdeal.kotlift.data.remote.ApiResponse
import com.therealdeal.kotlift.data.remote.RoutineExerciseDTO
import com.therealdeal.kotlift.data.remote.WorkoutDetailDTO
import com.therealdeal.kotlift.model.WorkoutDetail
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import com.therealdeal.kotlift.data.remote.ExerciseDTO
import com.therealdeal.kotlift.model.ExerciseInWorkout

class WorkoutDetailRepository(
    private val supabase: SupabaseClient,
    private val httpClient: HttpClient
) {

    suspend fun getWorkoutDetail(workoutId: String): Result<WorkoutDetail> {
        return runCatching {
            supabase.auth.currentUserOrNull() ?: error("User not authenticated")

            val workout = supabase.postgrest["workouts"]
                .select {
                    filter { eq("id", workoutId) }
                    limit(1)
                }
                .decodeSingle<WorkoutDetailDTO>()

            val routineExercises = supabase.postgrest["routine_exercises"]
                .select {
                    filter { eq("workout_id", workoutId) }
                    order("order_index", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                }
                .decodeList<RoutineExerciseDTO>()

            val exercisesWithDetails = coroutineScope {
                routineExercises.map { routineExercise ->
                    async {
                        val apiResponse = httpClient.get(
                            "https://oss.exercisedb.dev/api/v1/exercises/" + routineExercise.externalExerciseId
                        ).body<ApiResponse<ExerciseDTO>>()

                        ExerciseInWorkout(
                            routineExerciseId = routineExercise.id,
                            externalExerciseId = routineExercise.externalExerciseId,
                            orderIndex = routineExercise.orderIndex,
                            targetSets = routineExercise.targetSets,
                            targetReps = routineExercise.targetReps,
                            targetWeight = routineExercise.targetWeight,
                            exercise = apiResponse.data.toDomain()
                        )
                    }
                }.awaitAll()
            }

            WorkoutDetail(
                id = workout.id,
                name = workout.name,
                description = workout.description,
                difficulty = workout.difficulty,
                estimatedTimeMinutes = workout.estimatedTimeMinutes,
                createdAt = workout.createdAt,
                exercises = exercisesWithDetails.sortedBy { it.orderIndex }
            )
        }
    }
}