package com.entin.lighttasks.domain.entity

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.entin.lighttasks.presentation.util.ZERO
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
    @PrimaryKey(autoGenerate = true) val id: Int = ZERO,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "is_finished") val isFinished: Boolean,
    @ColumnInfo(name = "is_important") val isImportant: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "edited_at") val editedAt: Long,
    @ColumnInfo(name = "task_group") val group: Int,
    @ColumnInfo(name = "position") var position: Int,
    @ColumnInfo(name = "expire_date_first") var expireDateFirst: Long,
    @ColumnInfo(name = "expire_date_second") var expireDateSecond: Long,
    @ColumnInfo(name = "is_task_expired") val isTaskExpired: Boolean,
    @ColumnInfo(name = "is_event") val isEvent: Boolean,
    @ColumnInfo(name = "is_range") val isRange: Boolean,
) : Parcelable