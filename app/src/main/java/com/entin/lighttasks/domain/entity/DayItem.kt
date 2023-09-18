package com.entin.lighttasks.domain.entity

import java.time.DayOfWeek

data class DayItem(
    val dayNumber: Int,
    val listOfTasks: List<Task>,
    val isToday: Boolean,
    val dayOfWeek: DayOfWeek,
)