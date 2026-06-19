package com.therealdeal.kotlift.model

import java.util.UUID

data class Achievement(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    val functionName: String
)