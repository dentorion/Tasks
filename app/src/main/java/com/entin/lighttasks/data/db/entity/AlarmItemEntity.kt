package com.entin.lighttasks.data.db.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.ZERO_LONG

@Keep
@Entity(tableName = "alarms")
data class AlarmItemEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "alarm_id") val alarmId: Int = ZERO,
    @ColumnInfo(name = "alarm_time") val alarmTime: Long = ZERO_LONG,
    @ColumnInfo(name = "alarm_message") val alarmMessage: String = EMPTY_STRING,
    @ColumnInfo(name = "task_id") val taskId: Int = ZERO,
)
