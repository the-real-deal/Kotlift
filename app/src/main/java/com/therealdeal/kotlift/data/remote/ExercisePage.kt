package com.therealdeal.kotlift.data.remote

import com.therealdeal.kotlift.model.Exercise

data class ExercisePage(
    val exercises: List<Exercise>,
    val nextCursor: String?,
    val hasNextPage: Boolean
)