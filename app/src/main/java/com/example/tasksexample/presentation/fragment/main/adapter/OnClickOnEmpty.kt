package com.example.tasksexample.presentation.fragment.main.adapter

import com.example.tasksexample.domain.entity.Task

interface OnClickOnEmpty {
    fun onTaskClick(task: Task)
    fun onFinishedTaskClick(task: Task, mode: Boolean)
}