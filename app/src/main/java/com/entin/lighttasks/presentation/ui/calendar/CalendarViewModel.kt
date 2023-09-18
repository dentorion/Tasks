package com.entin.lighttasks.presentation.ui.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.domain.entity.CalendarDatesConstraints
import com.entin.lighttasks.domain.entity.DayItem
import com.entin.lighttasks.domain.entity.IconTask
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.CALENDAR_CONSTRAINTS
import com.entin.lighttasks.presentation.util.CALENDAR_ICON_SORT
import com.entin.lighttasks.presentation.util.LAST_HOUR
import com.entin.lighttasks.presentation.util.LAST_MINUTE
import com.entin.lighttasks.presentation.util.LAST_SECOND
import com.entin.lighttasks.presentation.util.ONE
import com.entin.lighttasks.presentation.util.THOUSAND
import com.entin.lighttasks.presentation.util.ZERO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneOffset
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel for CalendarFragment
 */

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: TasksRepository,
) : ViewModel() {

    private val _calendarChannel = Channel<CalendarEventContract>()
    val calendarChannel = _calendarChannel.receiveAsFlow()

    private val _iconGroupsChannel = Channel<List<IconTask>>()
    val iconGroupsChannel = _iconGroupsChannel.receiveAsFlow()

    private val calendar = Calendar.getInstance()
    private var month = getMonthNumber()
    private var year = getYearNumber()

    var sortIcon: Int? = state.get<Int>(CALENDAR_ICON_SORT)
        set(value) {
            field = value
            state[CALENDAR_CONSTRAINTS] = value

            viewModelScope.launch(Dispatchers.IO) {
                val startDate = getUnixTimeOfMonth(getMonthNumber(), getYearNumber(), TimeBorder.START)
                val finishDate = getUnixTimeOfMonth(getMonthNumber(), getYearNumber(), TimeBorder.FINISH)

                calendarDatesConstraints =
                    when (calendarDatesConstraints) {
                    is CalendarDatesConstraints.StartFinishInMonth -> {
                        if(value == null) {
                            CalendarDatesConstraints.StartFinishInMonth(startDate, finishDate, null)
                        } else {
                            CalendarDatesConstraints.StartFinishInMonth(startDate, finishDate, value)
                        }
                    }
                    is CalendarDatesConstraints.StartInMonth -> {
                        if(value == null) {
                            CalendarDatesConstraints.StartInMonth(startDate, finishDate, null)
                        } else {
                            CalendarDatesConstraints.StartInMonth(startDate, finishDate, value)
                        }

                    }
                }
                getTasksByConstraints()
            }
        }

    private var calendarDatesConstraints: CalendarDatesConstraints =
        state.get<CalendarDatesConstraints>(CALENDAR_CONSTRAINTS)
            ?: CalendarDatesConstraints.StartInMonth(
                getUnixTimeOfMonth(month, year, TimeBorder.START),
                getUnixTimeOfMonth(month, year, TimeBorder.FINISH),
                sortIcon
            )
        set(value) {
            field = value
            state[CALENDAR_CONSTRAINTS] = value
        }

    var strictMonth: Boolean = state[CALENDAR_CONSTRAINTS] ?: false
        set(value) {
            field = value
            state[CALENDAR_CONSTRAINTS] = value

            val startDate = getUnixTimeOfMonth(month, year, TimeBorder.START)
            val finishDate = getUnixTimeOfMonth(month, year, TimeBorder.FINISH)

            calendarDatesConstraints = if (value) {
                CalendarDatesConstraints.StartFinishInMonth(startDate, finishDate, sortIcon)
            } else {
                CalendarDatesConstraints.StartInMonth(startDate, finishDate, sortIcon)
            }

            viewModelScope.launch(Dispatchers.IO) {
                getTasksByConstraints()
            }
        }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Get all groups icons
            _iconGroupsChannel.send(repository.getTaskIconGroups())
            // Get all tasks by constraints
            getTasksByConstraints()
        }
    }

    fun setMonthCalendar(isPlus: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isPlus) {
                calendar.add(Calendar.MONTH, ONE)
            } else {
                calendar.add(Calendar.MONTH, -ONE)
            }
            month = getMonthNumber()
            year = getYearNumber()
            val startDate = getUnixTimeOfMonth(month, year, TimeBorder.START)
            val finishDate = getUnixTimeOfMonth(month, year, TimeBorder.FINISH)

            calendarDatesConstraints = when (calendarDatesConstraints) {
                is CalendarDatesConstraints.StartFinishInMonth ->
                    CalendarDatesConstraints.StartFinishInMonth(startDate, finishDate, sortIcon)

                is CalendarDatesConstraints.StartInMonth ->
                    CalendarDatesConstraints.StartInMonth(startDate, finishDate, sortIcon)
            }
            getTasksByConstraints()
        }
    }

    private suspend fun getTasksByConstraints() {
        repository.getTasksByConstraints(calendarDatesConstraints).first { listTasks ->
            generateDayItemList(listTasks, calendarDatesConstraints)
            true
        }
    }

    private fun getUnixTimeOfMonth(
        month: Int,
        year: Int,
        timeBorder: TimeBorder
    ): Long = when (timeBorder) {
        TimeBorder.START -> {
            LocalDateTime.of(
                LocalDate.now().withYear(year).withMonth(month).withDayOfMonth(ONE),
                LocalTime.of(ZERO, ZERO, ZERO)
            ).toEpochSecond(ZoneOffset.UTC) * THOUSAND
        }

        TimeBorder.FINISH -> {
            LocalDateTime.of(
                LocalDate.now().withYear(year).withMonth(month).withDayOfMonth(getLastDayOfMonth(month)),
                LocalTime.of(LAST_HOUR, LAST_MINUTE, LAST_SECOND)
            ).toEpochSecond(ZoneOffset.UTC) * THOUSAND
        }
    }

    private suspend fun generateDayItemList(
        tasks: List<Task>, constraints: CalendarDatesConstraints
    ) {
        _calendarChannel.send(
            CalendarEventContract.UpdateCalendarAndMonth(
                listOfDays = listOf(
                    DayItem(1, listOf(), false, DayOfWeek.MONDAY),
                    DayItem(2, listOf(), false, DayOfWeek.TUESDAY),
                    DayItem(3, listOf(), false, DayOfWeek.WEDNESDAY),
                    DayItem(4, listOf(), false, DayOfWeek.THURSDAY),
                    DayItem(5, listOf(), false, DayOfWeek.FRIDAY),
                    DayItem(6, listOf(), false, DayOfWeek.SATURDAY),
                    DayItem(7, listOf(), false, DayOfWeek.SUNDAY),

                    DayItem(8, listOf(), false, DayOfWeek.MONDAY),
                    DayItem(9, listOf(), false, DayOfWeek.TUESDAY),
                    DayItem(10, listOf(), false, DayOfWeek.WEDNESDAY),
                    DayItem(11, listOf(), false, DayOfWeek.THURSDAY),
                    DayItem(12, tasks, true, DayOfWeek.FRIDAY),
                    DayItem(13, listOf(), false, DayOfWeek.SATURDAY),
                    DayItem(14, listOf(), false, DayOfWeek.SUNDAY),

                    DayItem(15, listOf(), false, DayOfWeek.MONDAY),
                    DayItem(16, listOf(), false, DayOfWeek.TUESDAY),
                    DayItem(17, listOf(), false, DayOfWeek.WEDNESDAY),
                    DayItem(18, listOf(), false, DayOfWeek.THURSDAY),
                    DayItem(19, listOf(), false, DayOfWeek.FRIDAY),
                    DayItem(20, listOf(), false, DayOfWeek.SATURDAY),
                    DayItem(21, listOf(), false, DayOfWeek.SUNDAY),

                    DayItem(22, listOf(), false, DayOfWeek.MONDAY),
                    DayItem(23, listOf(), false, DayOfWeek.TUESDAY),
                    DayItem(24, listOf(), false, DayOfWeek.WEDNESDAY),
                    DayItem(25, listOf(), false, DayOfWeek.THURSDAY),
                    DayItem(26, listOf(), false, DayOfWeek.FRIDAY),
                    DayItem(27, listOf(), false, DayOfWeek.SATURDAY),
                    DayItem(28, listOf(), false, DayOfWeek.SUNDAY),

                    DayItem(29, listOf(), false, DayOfWeek.MONDAY),
                    DayItem(30, listOf(), false, DayOfWeek.TUESDAY),
                    DayItem(31, listOf(), false, DayOfWeek.WEDNESDAY),
                ),
                monthSequenceNumber = getMonthNumber()
            )
        )
    }

    private fun getMonthNumber() =
        calendar.get(Calendar.MONTH) + ONE

    private fun getYearNumber() =
        calendar.get(Calendar.YEAR)

    private fun getLastDayOfMonth(month: Int): Int {
        val yearMonth = YearMonth.now().withMonth(month)
        return yearMonth.lengthOfMonth()
    }

    companion object {
        enum class TimeBorder {
            START, FINISH
        }
    }
}
