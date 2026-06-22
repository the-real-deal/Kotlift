    package com.therealdeal.kotlift.model

    import java.time.Instant
    import java.util.UUID

    data class Profile(
        val id: String,
        val username: String? = null,
        val email: String? = null,
        val dayStreak: Int = 0,
        val totalSessions: Int = 0,
        val unlockedAchievementsCount: Int = 0
    )