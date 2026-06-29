package com.therealdeal.kotlift.data.repository

import com.therealdeal.kotlift.data.remote.EarnedAchievementDTO
import com.therealdeal.kotlift.data.remote.RunningSessionDTO
import com.therealdeal.kotlift.model.RunSession
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlin.time.Clock

class RunningRepository(
    private val client: SupabaseClient
) {
    suspend fun getSession(userId: String) : List<RunSession> {
        return client.postgrest["running_sessions"]
            .select {
                filter {
                    eq("profile_id", userId)
                }
            }.decodeList<RunningSessionDTO>()
            .map { it.toDomain() }

    }

    suspend fun publishNewSession(userId: String, runningSession: RunSession) {

    }
}