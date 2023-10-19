package com.entin.lighttasks.presentation.screens.dialogs.security


sealed class SecurityStateContract {
    object RepeatPassword: SecurityStateContract()
    object ErrorOnRepeatPassword: SecurityStateContract()
    object SuccessOnRepeatPassword: SecurityStateContract()
}
