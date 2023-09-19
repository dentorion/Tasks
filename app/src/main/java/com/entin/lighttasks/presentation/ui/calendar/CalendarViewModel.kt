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
import com.entin.lighttasks.presentation.util.getDayOfWeek
import com.entin.lighttasks.presentation.util.getLastDayOfMonth
import com.entin.lighttasks.presentation.util.getMonthNumber
import com.entin.lighttasks.presentation.util.getYearNumber
import com.entin.lighttasks.presentation.util.isToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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
    private var month = getMonthNumber(calendar)
    private var year = getYearNumber(calendar)

    var sortIcon: Int? = state.get<Int>(CALENDAR_ICON_SORT)
        set(value) {
            field = value
            state[CALENDAR_CONSTRAINTS] = value

            viewModelScope.launch(Dispatchers.IO) {
                val startDate = getUnixTimeOfMonth(getMonthNumber(calendar), getYearNumber(calendar), TimeBorder.START)
                val finishDate = getUnixTimeOfMonth(getMonthNumber(calendar), getYearNumber(calendar), TimeBorder.FINISH)

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
            month = getMonthNumber(calendar)
            year = getYearNumber(calendar)
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
            generateDayItemList(listTasks)
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
                LocalDate.now().withYear(year).withMonth(month)
                    .withDayOfMonth(getLastDayOfMonth(month)),
                LocalTime.of(LAST_HOUR, LAST_MINUTE, LAST_SECOND)
            ).toEpochSecond(ZoneOffset.UTC) * THOUSAND
        }
    }

    private suspend fun generateDayItemList(tasks: List<Task>) {
        val size = getLastDayOfMonth(month)
        val dayItemList = mutableListOf<DayItem>()
        for (dayNumber in ONE..size) {
            dayItemList.add(
                DayItem(
                    dayNumber = dayNumber,
                    listOfTasks = getTasksByDayNumber(tasks, dayNumber),
                    isToday = isToday(dayNumber, month, year),
                    dayOfWeek = getDayOfWeek(dayNumber, month, year)
                )
            )
        }
        _calendarChannel.send(
            CalendarEventContract.UpdateCalendarAndMonth(
                listOfDays = dayItemList, monthSequenceNumber = getMonthNumber(calendar)
            )
        )
    }

    private fun getTasksByDayNumber(tasks: List<Task>, dayNumber: Int): List<Task> =
        tasks.filter { task ->
            Timestamp(task.expireDateFirst).date == dayNumber
        }

    companion object {
        enum class TimeBorder {
            START, FINISH
        }
    }
}
