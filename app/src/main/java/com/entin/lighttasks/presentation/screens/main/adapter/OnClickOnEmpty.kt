package com.entin.lighttasks.presentation.screens.main.adapter

import com.entin.lighttasks.domain.entity.Task

interface OnClickOnEmpty {
    fun onTaskClick(task: Task)
    fun onFinishedTaskClick(task: Task, mode: Boolean)
}
