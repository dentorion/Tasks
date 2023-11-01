package com.entin.lighttasks.presentation.screens.addedit

/**
 * List of available operations for AddEditFragment:
 * - show error if title is blank
 * - navigate to AllTasksFragment with result: edited or added
 */

sealed class EditTaskEventContract {
    object ShowErrorBlankTitleAndMessage : EditTaskEventContract()
    object ShowErrorDatesPicked : EditTaskEventContract()
    object ShowErrorAlarmTime : EditTaskEventContract()
    object TaskNotSaved : EditTaskEventContract()
    object OnSuccessPasswordAdd : EditTaskEventContract()
    data class RefreshTagsVisibility(
        val url: Boolean = false,
        val photo: Boolean = false,
        val voice: Boolean = false,
        val galleryImages: Boolean = false,
    ) : EditTaskEventContract()
    data class NavBackWithResult(val typeNewOrEditorExist: Int) : EditTaskEventContract()
}
