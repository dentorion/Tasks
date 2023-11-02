package com.entin.lighttasks.presentation.screens.dialogs.security


sealed class SecurityStateContract {
    // Create password
    object RepeatPassword: SecurityStateContract()
    object ErrorOnRepeatPassword: SecurityStateContract()
    object SuccessOnCreatePassword: SecurityStateContract()
    object SuccessOnUpdatePassword: SecurityStateContract()
    // Check password
    object ErrorOnCheckPassword: SecurityStateContract()
    object SuccessOnCheckPassword: SecurityStateContract()
    // Error
    data class ErrorNotFoundSecurityItem(val line: Int): SecurityStateContract()
    data class ErrorSectionIdIsNull(val line: Int): SecurityStateContract()
    data class ErrorTaskIdIsNull(val line: Int): SecurityStateContract()
}
