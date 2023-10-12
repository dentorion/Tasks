package com.entin.lighttasks.presentation.screens.main.adapter

import com.entin.lighttasks.domain.entity.Task

interface OnClickOnEmpty {
    fun onTaskClick(taskEntity: Task)
    fun onFinishedTaskClick(taskEntity: Task, mode: Boolean)
}
