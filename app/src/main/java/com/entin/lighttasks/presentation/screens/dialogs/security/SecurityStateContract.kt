package com.entin.lighttasks.presentation.screens.dialogs.security


sealed class SecurityStateContract {
    // Create password
    object RepeatPassword: SecurityStateContract()
    object ErrorOnRepeatPassword: SecurityStateContract()
    object SuccessOnRepeatPassword: SecurityStateContract()
    // Check password
    object ErrorOnCheckPassword: SecurityStateContract()
    object SuccessOnCheckPassword: SecurityStateContract()
}
