package com.entin.lighttasks.data.db.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.entin.lighttasks.presentation.util.ZERO

@Keep
@Entity(tableName = "security")
data class SecurityEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = ZERO,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "task_id") val taskId: Int,
    @ColumnInfo(name = "section_id") val sectionId: Int,
)
