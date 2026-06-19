package com.therealdeal.kotlift.model

import java.time.Instant
import java.util.UUID

data class EarnedAchievement(
    val profileId: UUID,
    val achievementId: UUID,
    val unlockedAt: Instant = Instant.now()
)