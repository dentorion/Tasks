package com.entin.lighttasks.presentation.screens.dialogs.security

sealed class SecurityType {
    data class Check(val securityPlace: SecurityPlace): SecurityType()
    data class Create(val securityPlace: SecurityPlace): SecurityType()
}

enum class SecurityPlace {
    TASK,
    SECTION
}