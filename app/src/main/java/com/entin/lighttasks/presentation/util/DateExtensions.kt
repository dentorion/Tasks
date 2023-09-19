package com.entin.lighttasks.presentation.util

import com.entin.lighttasks.R
import com.entin.lighttasks.domain.entity.Months
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.toFormattedString(): String {
    val dateFormat = SimpleDateFormat(DATE_FORMAT_STANDARD, Locale.getDefault())
    return dateFormat.format(this)
}

fun Long.toFormattedDateString(): String {
    val date = Date(this)
    val dateFormat = SimpleDateFormat(DATE_FORMAT_STANDARD, Locale.getDefault())
    return dateFormat.format(date)
}

fun convertUnixTimestampToYearMonthDay(timestamp: Long): Triple<Int, Int, Int> {
    val dateFormat = SimpleDateFormat(DATE_FORMAT_STANDARD, Locale.getDefault())
    val date = Date(timestamp) // Convert seconds to milliseconds

    val formattedDate = dateFormat.format(date)
    val parts = formattedDate.split("-")

    val year = parts[0].toInt()
    val month = parts[1].toInt() - 1
    val day = parts[2].toInt()

    return Triple(year, month, day)
}

fun getMonthName(monthSequenceNumber: Int): Int =
    when (monthSequenceNumber) {
        Months.January.number -> R.string.january
        Months.February.number -> R.string.february
        Months.March.number -> R.string.march
        Months.April.number -> R.string.april
        Months.May.number -> R.string.maj
        Months.June.number -> R.string.june
        Months.July.number -> R.string.july
        Months.August.number -> R.string.august
        Months.September.number -> R.string.september
        Months.October.number -> R.string.october
        Months.November.number -> R.string.november
        Months.December.number -> R.string.december
        else -> R.string.error
    }

fun isToday(day: Int, month: Int, year: Int): Boolean =
    LocalDate.of(year, month, day).isEqual(LocalDate.now())

fun getDayOfWeek(day: Int, month: Int, year: Int): DayOfWeek =
    LocalDate.of(year, month, day).dayOfWeek

fun getMonthNumber(calendar: Calendar) = calendar.get(Calendar.MONTH) + ONE

fun getYearNumber(calendar: Calendar) = calendar.get(Calendar.YEAR)

fun getLastDayOfMonth(month: Int): Int = YearMonth.now().withMonth(month).lengthOfMonth()
