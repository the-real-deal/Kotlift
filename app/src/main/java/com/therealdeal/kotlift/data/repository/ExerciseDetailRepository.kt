package com.therealdeal.kotlift.data.repository

import com.therealdeal.kotlift.data.remote.ApiResponse
import com.therealdeal.kotlift.data.remote.ExerciseDTO
import com.therealdeal.kotlift.data.remote.ExercisePage
import com.therealdeal.kotlift.data.remote.MuscleDTO
import com.therealdeal.kotlift.data.remote.PaginatedResponse
import com.therealdeal.kotlift.model.ExerciseDetail
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

private const val BASE_URL = "https://oss.exercisedb.dev/api/v1"

class ExerciseDetailRepository(
    private val httpClient: HttpClient
) {

    suspend fun getExerciseDetail(
        exerciseId:String
    ): Result<ExerciseDetail> {
        return runCatching {
            val apiResponse = httpClient.get("$BASE_URL/exercises/${exerciseId}") {
                parameter("limit", 1)
            }.body<ApiResponse<ExerciseDTO>>()

            val exerciseDTO = apiResponse.data
            exerciseDTO.toDomainWithDetail()
        }
    }
}