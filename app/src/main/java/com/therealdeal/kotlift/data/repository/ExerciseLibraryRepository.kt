package com.therealdeal.kotlift.data.repository

import com.therealdeal.kotlift.data.remote.ApiResponse
import com.therealdeal.kotlift.data.remote.ExerciseDTO
import com.therealdeal.kotlift.data.remote.ExercisePage
import com.therealdeal.kotlift.data.remote.MuscleDTO
import com.therealdeal.kotlift.data.remote.PaginatedResponse
import com.therealdeal.kotlift.model.Exercise
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

private const val BASE_URL = "https://oss.exercisedb.dev/api/v1"
private const val PAGE_SIZE = 10

class ExerciseLibraryRepository(
    private val httpClient: HttpClient
) {

    suspend fun getMuscles(): Result<List<String>> {
        return runCatching {
            httpClient.get("$BASE_URL/muscles")
                .body<ApiResponse<List<MuscleDTO>>>()
                .data
                .map { it.name }
        }
    }

    suspend fun getExercises(
        cursor: String? = null,
        targetMuscles: List<String> = emptyList(),
        bodyParts: List<String> = emptyList(),
        equipments: List<String> = emptyList(),
        query: String? = null
    ): Result<ExercisePage> {
        return runCatching {
            val response = httpClient.get("$BASE_URL/exercises") {
                parameter("limit", PAGE_SIZE)
                cursor?.let { parameter("after", it) }
                query?.takeIf { it.isNotBlank() }?.let { parameter("name", it) }
                targetMuscles.takeIf { it.isNotEmpty() }?.let { parameter("targetMuscles", it.joinToString(",")) }
                bodyParts.takeIf { it.isNotEmpty() }?.let { parameter("bodyParts", it.joinToString(",")) }
                equipments.takeIf { it.isNotEmpty() }?.let { parameter("equipments", it.joinToString(",")) }
            }.body<PaginatedResponse<ExerciseDTO>>()

            ExercisePage(
                exercises = response.data.map { it.toDomain() },
                nextCursor = response.meta?.nextCursor,
                hasNextPage = response.meta?.hasNextPage ?: false
            )
        }
    }

    suspend fun getExerciseById(id: String): Result<Exercise> {
        return runCatching {
            httpClient.get("$BASE_URL/exercises/$id")
                .body<ApiResponse<ExerciseDTO>>()
                .data
                .toDomain()
        }
    }
}