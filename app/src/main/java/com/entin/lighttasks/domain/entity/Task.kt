package com.entin.lighttasks.domain.entity

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import com.entin.lighttasks.data.db.entity.TaskEntity
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.ZERO_LONG
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    @ColumnInfo(name = "id") val id: Int = ZERO,
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
    @ColumnInfo(name = "attached_link") val attachedLink: String,
    @ColumnInfo(name = "attached_photo") val attachedPhoto: String,
    @ColumnInfo(name = "attached_voice") val attachedVoice: String,
    @ColumnInfo(name = "section_id") val sectionId: Int,
    @ColumnInfo(name = "alarm_time") val alarmTime: Long = ZERO_LONG,    // Join from Alarms
    @ColumnInfo(name = "attached_gallery_images") val attachedGalleryImages: List<Uri>
): Parcelable

fun Task.toTaskEntity(alarmId: Long): TaskEntity = TaskEntity(
    id = this.id,
    title = this.title,
    message = this.message,
    isFinished = this.isFinished,
    isImportant = this.isImportant,
    createdAt = this.createdAt,
    editedAt = this.editedAt,
    group = this.group,
    position = this.position,
    expireDateFirst = this.expireDateFirst,
    expireDateSecond = this.expireDateSecond,
    isTaskExpired = this.isTaskExpired,
    isEvent = this.isEvent,
    isRange = this.isRange,
    attachedLink = this.attachedLink,
    attachedPhoto = this.attachedPhoto,
    attachedVoice = this.attachedVoice,
    sectionId = this.sectionId,
    alarmId = alarmId,
    attachedGalleryImages = this.attachedGalleryImages,
)