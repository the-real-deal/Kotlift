package com.therealdeal.kotlift.data.remote

import com.therealdeal.kotlift.model.RunSession
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
class RunningSessionDTO (
    @SerialName("id") val id: String?,
    @SerialName("created_at") val date: Instant,
    @SerialName("meters_run") val meters: Int,
    @SerialName("seconds_run") val seconds: Int,
    @SerialName("profile_id") val profileId: String
) {
    fun toDomain(): RunSession {
        return RunSession(
            id = id!!,
            date = date,
            distanceKm = meters / 1000.0,
            durationSeconds = seconds
        )
    }
}

@Serializable
class RunningSessionRequestDTO (
    @SerialName("created_at") val date: String,
    @SerialName("meters_run") val meters: Int,
    @SerialName("seconds_run") val seconds: Int,
    @SerialName("profile_id") val profileId: String
)