package com.therealdeal.kotlift.data.repository

import com.therealdeal.kotlift.data.remote.RunningSessionDTO
import com.therealdeal.kotlift.data.remote.RunningSessionRequestDTO
import com.therealdeal.kotlift.model.RunSession
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class RunningRepository(
    private val client: SupabaseClient
) {
    private val _invalidate = MutableSharedFlow<Unit>(replay = 1).also { it.tryEmit(Unit) }

    fun invalidate() {
        _invalidate.tryEmit(Unit)
    }

    suspend fun getSessionResult(userId: String) : Result<List<RunSession>> {
        return runCatching {
            client.postgrest["running_sessions"]
                .select {
                    filter {
                        eq("profile_id", userId)
                    }
                }.decodeList<RunningSessionDTO>()
                .map { it.toDomain() }
        }
    }

    suspend fun publishNewSession(userId: String, runningSession: RunSession) : Result<RunSession> {
        return runCatching {
            val runningSession = client.postgrest["running_sessions"]
                .insert(
                    RunningSessionRequestDTO(
                        date =  runningSession.date.toString(),
                        meters = (runningSession.distanceKm*1000).toInt(),
                        seconds = runningSession.durationSeconds,
                        profileId = userId
                    )
                ) { select() }
                .decodeSingle<RunningSessionDTO>()
                .toDomain()
            invalidate()
            runningSession
        }
    }
}