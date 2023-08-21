package com.entin.lighttasks.presentation.ui.addedit.contract

/**
 * List of available operations for AddEditFragment:
 * - show error if title is blank
 * - navigate to AllTasksFragment with result: edited or added
 */

sealed class EditTaskEvent {
    object ShowErrorBlankTitleText : EditTaskEvent()
    data class NavBackWithResult(val typeNewOrEditorExist: Int) : EditTaskEvent()
}