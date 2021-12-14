package com.entin.lighttasks.domain.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Entity(tableName = "tasks")
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "task_title") val title: String,
    @ColumnInfo(name = "task_message") val message: String,
    @ColumnInfo(name = "task_finished") val finished: Boolean = false,
    @ColumnInfo(name = "task_important") val important: Boolean = false,
    @ColumnInfo(name = "date_created") val date: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "task_group") val group: Int,
    @ColumnInfo(name = "task_position") var position: Int = Int.MAX_VALUE,
) : Parcelable {

    val dateFormat: String
        get() = DateFormat.getDateTimeInstance().format(date)
}

