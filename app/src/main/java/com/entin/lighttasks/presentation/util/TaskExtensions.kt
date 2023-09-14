package com.entin.lighttasks.presentation.util

import android.content.res.Resources
import com.entin.lighttasks.R

fun checkForEmptyTitle(taskTitle: String, resources: Resources, id: Int? = null): String {
    return if (taskTitle == EMPTY_STRING) {
        "${resources.getString(R.string.no_name_title_task)} ${id?.let { "#$it" }}"
    } else {
        taskTitle
    }
}