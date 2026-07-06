package com.therealdeal.kotlift.model

import kotlinx.datetime.LocalDate

data class Stats(
    val totalWeightLifted: Double,
    val totalSessions: Int,
    val avgSessionMinutes: Double,
    val last7DaysActivity: List<Pair<LocalDate, Double>>,
    val mostDoneWorkout: WorkoutFrequency?,
    val allSessions: List<Session>
)

data class WorkoutFrequency(
    val workoutId: String,
    val name: String,
    val count: Int
)