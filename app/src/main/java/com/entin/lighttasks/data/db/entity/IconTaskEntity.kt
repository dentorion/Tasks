package com.entin.lighttasks.data.db.entity

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Domain model TaskGroups
 * - represents item that user use to identify group of task
 * - need to bee @keep to not be shrinks by Proguard
 */

@Keep
@Entity(tableName = "taskGroups")
data class IconTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "background_resource") @DrawableRes val backgroundRes: Int,
    @ColumnInfo(name = "group_id") val groupId: Int,
)
