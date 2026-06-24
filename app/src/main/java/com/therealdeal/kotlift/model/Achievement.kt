package com.therealdeal.kotlift.model

import kotlin.time.Instant

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val functionName: String,
    val isCompleted: Boolean,
    val completedAt: Instant?,
    val threshold: Int
)
