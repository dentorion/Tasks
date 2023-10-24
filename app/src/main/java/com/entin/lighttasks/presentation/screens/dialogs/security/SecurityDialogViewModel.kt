package com.entin.lighttasks.presentation.screens.dialogs.security

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.domain.repository.SecurityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Security dialog
 */

@HiltViewModel
class SecurityDialogViewModel @Inject constructor(
    private val securityRepository: SecurityRepository,
    val state: SavedStateHandle,
) : ViewModel() {

    val action: MutableLiveData<SecurityStateContract> by lazy {
        MutableLiveData<SecurityStateContract>()
    }

    var insertedPassword: String? = null

    fun onSubmitClicked(
        securityType: SecurityType,
        passwordFromUser: String,
        securityItemId: Int? = null
    ) {
        when (securityType) {
            is SecurityType.Check -> {
                when (securityType.securityPlace) {
                    SecurityPlace.TASK -> checkPasswordCode(securityItemId, passwordFromUser)

                    SecurityPlace.SECTION -> checkPasswordCode(securityItemId, passwordFromUser)
                }
            }

            is SecurityType.Create -> {
                when (securityType.securityPlace) {
                    SecurityPlace.TASK -> createPasswordCode(passwordFromUser)

                    SecurityPlace.SECTION -> createPasswordCode(passwordFromUser)
                }
            }
        }
    }

    private fun createPasswordCode(passwordFromUser: String) {
        viewModelScope.launch {
            if (insertedPassword == null) {
                insertedPassword = passwordFromUser
                action.value = SecurityStateContract.RepeatPassword
            } else {
                if (insertedPassword.equals(passwordFromUser)) {
                    insertedPassword = null
                    action.value = SecurityStateContract.SuccessOnRepeatPassword
                } else {
                    insertedPassword = null
                    action.value = SecurityStateContract.ErrorOnRepeatPassword
                }
            }
        }
    }

    private fun checkPasswordCode(securityItemId: Int?, passwordFromUser: String) {
        viewModelScope.launch(Dispatchers.IO) {
            securityItemId?.let { securityId ->
                val realPassword = securityRepository.getSecurityItemById(securityId)
                    .first()
                    .password
                if (realPassword == passwordFromUser) {
                    action.postValue(SecurityStateContract.SuccessOnCheckPassword)
                } else {
                    action.postValue(SecurityStateContract.ErrorOnCheckPassword)
                }
            }
        }
    }

    override fun onCleared() {
        insertedPassword = null
        super.onCleared()
    }
}
