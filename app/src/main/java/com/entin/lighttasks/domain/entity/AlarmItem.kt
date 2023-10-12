package com.entin.lighttasks.domain.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.entin.lighttasks.presentation.util.ZERO

@Keep
@Entity(tableName = "alarms")
data class AlarmItem(
    @PrimaryKey(autoGenerate = true) val id: Int = ZERO,
    @ColumnInfo(name = "time") val time: Long,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "task_id") val taskId: Int,
)
