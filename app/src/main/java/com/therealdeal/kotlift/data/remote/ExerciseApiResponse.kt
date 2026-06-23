package com.therealdeal.kotlift.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class PaginationMeta(
    val total: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val nextCursor: String? = null
)

@Serializable
data class PaginatedResponse<T>(
    val success: Boolean,
    val data: List<T>,
    val meta: PaginationMeta? = null
)