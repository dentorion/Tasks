package com.entin.lighttasks.domain.entity

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.entin.lighttasks.presentation.util.ZERO_LONG
import kotlinx.parcelize.Parcelize

/**
 * Domain model Task
 * - represents item that user creates and use
 * - need to bee @keep to not be shrinks by Proguard
 */

@Keep
@Entity(tableName = "tasks")
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "is_finished") val finished: Boolean = false,
    @ColumnInfo(name = "is_important") val important: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "task_group") val group: Int,
    @ColumnInfo(name = "position") var position: Int = Int.MAX_VALUE,
    @ColumnInfo(name = "expire_date_first") var expireDateFirst: Long = ZERO_LONG,
    @ColumnInfo(name = "expire_date_second") var expireDateSecond: Long = ZERO_LONG,
    @ColumnInfo(name = "is_task_expired") val isTaskExpired: Boolean = false,
    @ColumnInfo(name = "is_event") val isEvent: Boolean = false,
    @ColumnInfo(name = "is_range") val isRange: Boolean = false,
) : Parcelable
