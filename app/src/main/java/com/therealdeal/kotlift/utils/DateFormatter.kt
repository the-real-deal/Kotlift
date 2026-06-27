package com.therealdeal.kotlift.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Instant

fun formatDate(date: Instant?) : String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val formatted = date
        ?.toLocalDateTime(TimeZone.currentSystemDefault())
        ?.date
        ?.toJavaLocalDate()
        ?.format(formatter)?: "No date"

    return formatted
}

fun Int.formatDuration(): String {
    val h = this / 3600
    val m = (this % 3600) / 60
    val s = this % 60
    return "%02d:%02d:%02d".format(h, m, s)
}