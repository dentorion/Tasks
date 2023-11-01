package com.entin.lighttasks.presentation.screens.dialogs.security

import android.os.Parcelable
import com.entin.lighttasks.domain.entity.Task
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class SecurityType: Parcelable {
    data class Check(val securityPlace: SecurityPlace): SecurityType()
    data class Create(val securityPlace: SecurityPlace): SecurityType()
}

@Parcelize
sealed class SecurityPlace: Parcelable {
    data class TaskPlace(val task: Task?): SecurityPlace()
    data class SectionPlace(val sectionId: Int?): SecurityPlace()
}