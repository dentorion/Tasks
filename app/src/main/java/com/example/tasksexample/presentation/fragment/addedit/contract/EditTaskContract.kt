package com.example.tasksexample.presentation.fragment.addedit.contract

sealed class EditTaskEvent {
    object ShowErrorBlankTitleText : EditTaskEvent()
    data class NavBackWithResult(val typeNewOrEdit: Int) : EditTaskEvent()
}