package com.example.tasksexample.presentation.fragment.main.contract

import com.example.tasksexample.domain.entity.Task

sealed class AllTasksEvent {
    object NavToNewTask : AllTasksEvent()
    data class NavToEditTask(val task: Task) : AllTasksEvent()
    data class ShowUndoDeleteTaskMessage(val task: Task) : AllTasksEvent()
    data class ShowAddEditTaskMessage(val type: AddEditTaskMessage) : AllTasksEvent()
    object NavToDellFinishedTasks : AllTasksEvent()
    object ShowDellFinishedTasks : AllTasksEvent()
    object NavToChangeLanguage : AllTasksEvent()
}

enum class AddEditTaskMessage {
    EDIT,
    NEW
}