package com.entin.lighttasks.presentation.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

fun Date.toFormattedString(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    return dateFormat.format(this)
}

fun Long.toFormattedDateString(): String {
    val date = Date(this)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    return dateFormat.format(date)
}

fun convertUnixTimestampToYearMonthDay(timestamp: Long): Triple<Int, Int, Int> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = Date(timestamp) // Convert seconds to milliseconds

    val formattedDate = dateFormat.format(date)
    val parts = formattedDate.split("-")

    val year = parts[0].toInt()
    val month = parts[1].toInt()-1
    val day = parts[2].toInt()

    return Triple(year, month, day)
}