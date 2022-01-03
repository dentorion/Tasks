package com.entin.lighttasks.presentation.util

import com.entin.lighttasks.domain.entity.Task
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toDomainModel() = Task(
    title = this.getString(TITLE) ?: "",
    message = this.getString(MESSAGE) ?: "",
    finished = this.getBoolean(FINISHED) ?: false,
    important = this.getBoolean(IMPORTANT) ?: false,
    date = this.getLong(DATE) ?: System.currentTimeMillis(),
    group = this.getLong(GROUP)?.toInt() ?: 0,
)

private const val TITLE = "title"
private const val MESSAGE = "message"
private const val FINISHED = "finished"
private const val IMPORTANT = "important"
private const val DATE = "date"
private const val GROUP = "group"