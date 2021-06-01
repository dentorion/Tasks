package com.example.tasksexample.presentation.ui.addedit.contract

sealed class EditTaskEvent {
    object ShowErrorBlankTitleText : EditTaskEvent()
    data class NavBackWithResult(val typeNewOrEdit: Int) : EditTaskEvent()
}