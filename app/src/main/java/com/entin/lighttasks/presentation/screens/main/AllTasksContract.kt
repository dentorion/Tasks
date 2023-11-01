package com.entin.lighttasks.presentation.screens.main

import com.entin.lighttasks.domain.entity.Task

/**
 * Available events for AllTasksFragment
 */

sealed class AllTasksEvent {

    // Navigate to new / edit task fragment
    object NavToNewTask : AllTasksEvent()

    // Navigate to new / edit task fragment
    data class NavToEditTask(val task: Task) : AllTasksEvent()

    // Navigate to new / edit task fragment from security code checking
    data class NavToEditTaskFromSecurity(val task: Task) : AllTasksEvent()

    // Navigate to Dialog delete finished tasks
    object NavToDellFinishedTasks : AllTasksEvent()

    // Navigate to Dialog show finished tasks
    object ShowDellFinishedTasks : AllTasksEvent()

    // Navigate to Dialog change language
    object NavToChangeLanguage : AllTasksEvent()

    // Navigate to preferences
    object NavToChangePreferences : AllTasksEvent()

    // Navigate to calendar
    object NavToCalendar : AllTasksEvent()

    // Navigate to section preferences
    object NavToSectionPreferences : AllTasksEvent()

    // Check password code for security item by task id
    data class CheckPasswordTask(val task: Task): AllTasksEvent()

    // Check password code for security item by section id
    data class CheckPasswordSection(val sectionId: Int): AllTasksEvent()

    // Show snackBar undo delete task
    data class ShowUndoDeleteTaskMessage(val task: Task) : AllTasksEvent()

    // Show snackBar if task was deleted and returned with UNDO button show snackBar smile
    object RestoreTaskWithoutPhoto : AllTasksEvent()
}
