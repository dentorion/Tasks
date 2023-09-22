package com.entin.lighttasks.presentation.activity

/**
 * Available events for AllTasksFragment
 */

sealed class MainActivityEvent {

    // Return count tasks for widget
    data class CountTasksWidget(val count: Int) : MainActivityEvent()
}
