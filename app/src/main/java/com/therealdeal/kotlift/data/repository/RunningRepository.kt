package com.therealdeal.kotlift.data.repository

import com.therealdeal.kotlift.model.RunSession
import io.github.jan.supabase.SupabaseClient
import kotlin.time.Clock

class RunningRepository(
    supabaseClient: SupabaseClient
) {
    suspend fun getSession(userId: String) : List<RunSession> {
        // [TODO] get from db
        val time = Clock.System.now()
        return listOf(
            RunSession( "1", time, 15.31, 10726),
            RunSession( "2", time, 5.83, 3460),
            RunSession( "3", time, 1.89, 1534),
            RunSession( "4", time, 6.32, 4923))
    }

    suspend fun publishNewSession(userId: String, runningSession: RunSession) {
        // push to supabase
    }
}