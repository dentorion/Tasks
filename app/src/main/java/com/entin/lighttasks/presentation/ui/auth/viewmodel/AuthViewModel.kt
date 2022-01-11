package com.entin.lighttasks.presentation.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.domain.repository.LogTag
import com.entin.lighttasks.domain.repository.LoggerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Auth Fragment
 */

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loggerRepository: LoggerRepository
) : ViewModel() {

    fun logSuccessAuth() = viewModelScope.launch {
        log(message = "Success", tag = LogTag.Auth.LogIn(), exception = null)
    }

    fun logFailureAuth(exception: Throwable? = null) = viewModelScope.launch {
        log(message = "Failure", tag = LogTag.Auth.LogIn(), exception = exception)
    }

    private suspend fun log(message: String, tag: LogTag, exception: Throwable?) {
        loggerRepository.log(message = message, tag = tag, exception = exception)
    }
}