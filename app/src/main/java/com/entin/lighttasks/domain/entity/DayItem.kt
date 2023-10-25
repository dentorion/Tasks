package com.entin.lighttasks.domain.entity

import androidx.annotation.Keep
import java.time.DayOfWeek

@Keep
data class DayItem(
    val dayNumber: Int,
    val listOfTaskEntities: List<Task>,
    val isToday: Boolean,
    val dayOfWeek: DayOfWeek,
)