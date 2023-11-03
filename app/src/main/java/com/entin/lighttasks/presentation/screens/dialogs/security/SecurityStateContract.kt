package com.entin.lighttasks.presentation.screens.dialogs.security


sealed class SecurityStateContract {
    // Create password
    object RepeatPassword: SecurityStateContract()
    object ErrorOnRepeatPassword: SecurityStateContract()
    object SuccessOnCreatePassword: SecurityStateContract()
    object SuccessOnUpdatePassword: SecurityStateContract()
    data class CreateTaskPassword(val passwordFromUser: String): SecurityStateContract()
    data class UpdateTaskPassword(val passwordFromUser: String): SecurityStateContract()
    // Check password
    object ErrorOnCheckPassword: SecurityStateContract()
    object SuccessOnCheckPassword: SecurityStateContract()
    // Error
    object ErrorNotFoundSecurityItemToCheckPassword: SecurityStateContract()
    object ErrorSectionIdIsNull: SecurityStateContract()
    object ErrorTaskIdIsNull: SecurityStateContract()
}
