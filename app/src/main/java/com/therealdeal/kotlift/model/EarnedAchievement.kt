package com.therealdeal.kotlift.model

import java.time.Instant
import java.util.UUID

data class EarnedAchievement(
    val profileId: String,
    val achievementId: String,
    val unlockedAt: Instant = Instant.now()
)