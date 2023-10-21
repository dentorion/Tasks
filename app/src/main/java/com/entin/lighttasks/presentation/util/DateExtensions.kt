@file:Suppress("DEPRECATION")

package com.entin.lighttasks.presentation.util

import com.entin.lighttasks.R
import com.entin.lighttasks.domain.entity.Months
import com.entin.lighttasks.presentation.screens.addedit.AddEditTaskViewModel
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Long.toFormattedDateString(): String {
    val date = Date(this)
    val dateFormat = SimpleDateFormat(DATE_FORMAT_STANDARD, Locale.getDefault())
    return dateFormat.format(date)
}

fun Long.toFormattedDateTimeString(): String {
    val date = Date(this)
    val dateFormat = SimpleDateFormat(DATE_FORMAT_STANDARD_TIME, Locale.getDefault())
    return dateFormat.format(date)
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

fun replaceZeroDateWithNow(date: Long): Long =
    if (date == ZERO_LONG) getTimeMls() else date

fun getStartDate(
    year: Int,
    month: Int,
    day: Int,
): Long =
    getTimeMls(
        year = year - 1900,
        month = month,
        day = day,
        hours = ZERO,
        minutes = ONE,
        seconds = ONE,
    )

fun getFinishDate(
    year: Int,
    month: Int,
    day: Int,
): Long =
    getTimeMls(
        year = year - 1900,
        month = month,
        day = day,
        hours = LAST_HOUR,
        minutes = LAST_MINUTE,
        seconds = LAST_SECOND - ONE,
    )

fun getTimeMls(
    year: Int = Date().year,
    month: Int = Date().month,
    day: Int = Date().date,
    hours: Int = ZERO,
    minutes: Int = ZERO,
    seconds: Int = ZERO,
): Long {
    return Date().apply {
        setYear(year)
        setMonth(month)
        date = day
        setHours(hours)
        setMinutes(minutes)
        setSeconds(seconds)
    }.time
}

fun getCurrentYear() =
    Date().year + 1900

fun getCurrentMonth() =
    Date().month + ONE

fun getCurrentDay() =
    Date().date

fun getDefaultStartDateTime(): Long =
    getTimeMls(
        hours = ZERO,
        minutes = ONE,
        seconds = ONE,
    )

fun getDefaultFinishDateTime(): Long =
    getTimeMls(
        hours = LAST_HOUR,
        minutes = LAST_MINUTE,
        seconds = LAST_SECOND - ONE,
    ) + AddEditTaskViewModel.ONE_DAY_MLS