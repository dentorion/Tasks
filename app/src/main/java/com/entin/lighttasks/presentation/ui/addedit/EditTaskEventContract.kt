package com.entin.lighttasks.presentation.ui.addedit

/**
 * List of available operations for AddEditFragment:
 * - show error if title is blank
 * - navigate to AllTasksFragment with result: edited or added
 */

sealed class EditTaskEventContract {
    object ShowErrorBlankTitleText : EditTaskEventContract()
    object ShowErrorDatesPicked : EditTaskEventContract()
    data class NavBackWithResult(val typeNewOrEditorExist: Int) : EditTaskEventContract()
}
