package com.entin.lighttasks.domain.entity

import java.time.LocalDateTime

data class AlarmItem(
    val time: LocalDateTime,
    val message: String,
    val taskId: Int,
)
