package com.therealdeal.kotlift.model

import java.time.Instant
import java.util.UUID

data class Profile(
    val id: UUID, //ref auth.users
    val updatedAt: Instant? = null,
    val profilePicture: String? = null,
    val dayStreak: Int = 0,
    val totalSessions: Int = 0,
    val unlockedAchievementsCount: Int = 0
)