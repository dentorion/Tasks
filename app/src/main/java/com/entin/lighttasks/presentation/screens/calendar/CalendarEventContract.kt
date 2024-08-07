package com.entin.lighttasks.presentation.screens.calendar

import com.entin.lighttasks.domain.entity.DayItem

/**
 * Contract for Calendar
 */

sealed class CalendarEventContract {

    data class UpdateCalendarAndMonth(
        val listOfDays: List<DayItem>,
        val monthSequenceNumber: Int
    ) : CalendarEventContract()
}
