package com.entin.lighttasks.domain.entity

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import com.entin.lighttasks.data.db.entity.SectionEntity
import com.entin.lighttasks.presentation.util.ZERO
import kotlinx.parcelize.Parcelize

/**
 * Section it is SectionEntity + hasPassword (LEFT JOIN FROM Security table)
 */

@Parcelize
@Keep
data class Section(
    @ColumnInfo(name = "id") val id: Int = ZERO,
    @ColumnInfo(name = "name") val title: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "edited_at") val editedAt: Long,
    @ColumnInfo(name = "icon") val icon: Int,
    @ColumnInfo(name = "is_important") val isImportant: Boolean,
    @ColumnInfo(name = "position") var position: Int,
    @ColumnInfo(name = "has_password") val hasPassword: Boolean = false, // Join from Security
) : Parcelable

fun Section.toSectionEntity(): SectionEntity = SectionEntity(
    id = this.id,
    title = this.title,
    createdAt = this.createdAt,
    editedAt = this.editedAt,
    icon = this.icon,
    isImportant = this.isImportant,
    position = this.position
)