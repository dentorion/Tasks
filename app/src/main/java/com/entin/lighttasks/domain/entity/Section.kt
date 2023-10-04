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
data class Section(
    @PrimaryKey(autoGenerate = true) val id: Int = ZERO,
    @ColumnInfo(name = "name") val title: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "edited_at") val editedAt: Long,
    @ColumnInfo(name = "icon") val group: Int,
    @ColumnInfo(name = "is_important") val isImportant: Boolean,
    @ColumnInfo(name = "position") var position: Int,
) : Parcelable