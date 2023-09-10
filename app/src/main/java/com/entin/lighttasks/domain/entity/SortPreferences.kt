package com.entin.lighttasks.domain.entity

data class SortPreferences(
    val sortByTitleDateImportantManual: OrderSort,
    val hideFinished: Boolean,
    val sortASC: Boolean,
    val hideDatePickedTasks: Boolean,
)
