package com.entin.lighttasks.domain.entity

import androidx.annotation.Keep

@Keep
data class SortPreferences(
    val sortByTitleDateImportantManual: OrderSort,
    val hideFinished: Boolean,
    val sortASC: Boolean,
    val hideEvents: Boolean,
    val sectionId: Int,
)
