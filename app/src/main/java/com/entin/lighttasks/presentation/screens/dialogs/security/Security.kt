package com.entin.lighttasks.presentation.screens.dialogs.security

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class Security: Parcelable {
    data class Check(val place: Place, val purpose: SecurityPurpose): Security()
    data class Create(val place: Place, val purpose: SecurityPurpose): Security()
}

@Parcelize
sealed class Place: Parcelable {
    data class TaskPlace(val taskId: Int): Place()
    data class SectionPlace(val sectionId: Int): Place()
}

/**
 * Used to understand for what kind of operation password was created or edited
 * Fragment will invoke necessary function based on purpose
 */
enum class SecurityPurpose {
    // Create
    CREATE_TASK_PASSWORD,
    UPDATE_TASK_PASSWORD,
    CREATE_SECTION_PASSWORD,

    // Check
    CHECK_TASK_OPEN,
    CHECK_SECTION_TASKS_SHOW,
    CHECK_SECTION_DELETE,
    CHECK_SECTION_PASSWORD_DELETE
}