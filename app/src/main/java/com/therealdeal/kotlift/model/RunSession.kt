package com.therealdeal.kotlift.model

import org.osmdroid.util.GeoPoint
import kotlin.time.Clock
import kotlin.time.Instant

data class RunSession(
    val id: String,
    val date: Instant,
    val distanceKm: Double,
    val durationSeconds: Int
)

data class Track(
    var startTime: Instant = Clock.System.now(),
    var distanceKm: Double = 0.0,
    val points: List<GeoPoint> = emptyList()
)