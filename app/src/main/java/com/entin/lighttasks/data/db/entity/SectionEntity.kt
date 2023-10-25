package com.entin.lighttasks.data.db.entity

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.entin.lighttasks.presentation.util.ZERO
import kotlinx.parcelize.Parcelize

/**
 * Database model of Section
 */

@Keep
@Entity(tableName = "sections")
@Parcelize
data class SectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = ZERO,
    @ColumnInfo(name = "name") val title: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "edited_at") val editedAt: Long,
    @ColumnInfo(name = "icon") val icon: Int,
    @ColumnInfo(name = "is_important") val isImportant: Boolean,
    @ColumnInfo(name = "position") var position: Int,
) : Parcelable
