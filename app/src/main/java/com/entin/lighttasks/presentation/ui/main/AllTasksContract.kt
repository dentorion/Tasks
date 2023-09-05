package com.entin.lighttasks.presentation.ui.main

import com.entin.lighttasks.domain.entity.Task

/**
 * Available events for AllTasksFragment
 */

sealed class AllTasksEvent {

    // Navigate to new / edit task fragment
    object NavToNewTask : AllTasksEvent()

    // Navigate to new / edit task fragment
    data class NavToEditTask(val task: Task) : AllTasksEvent()

    // Navigate to Dialog delete finished tasks
    object NavToDellFinishedTasks : AllTasksEvent()

    // Navigate to Dialog show finished tasks
    object ShowDellFinishedTasks : AllTasksEvent()

    // Navigate to Dialog change language
    object NavToChangeLanguage : AllTasksEvent()

    // Navigate to preferences
    object NavToChangePreferences : AllTasksEvent()

    // Show snackBar undo delete task
    data class ShowUndoDeleteTaskMessage(val task: Task) : AllTasksEvent()

    // Show snackBar after add / edit task
    data class ShowAddEditTaskMessage(val type: AddEditTaskMessage) : AllTasksEvent()

    // Show snackBar if task was deleted and returned with UNDO button show snackBar smile
    object Smile : AllTasksEvent()
}

/**
 * Task was:
 * - edited
 * - created
 */
enum class AddEditTaskMessage {
    EDIT,
    NEW,
}
