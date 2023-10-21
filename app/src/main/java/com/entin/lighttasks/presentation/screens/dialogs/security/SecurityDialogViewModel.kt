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
        type: SecurityType,
        passwordFromUser: String,
        securityItemId: Int? = null
    ) {
        when (type) {
            is SecurityType.Check -> {
                when (type.securityPlace) {
                    SecurityPlace.TASK -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            securityItemId?.let { securityId ->
                                val realPassword = securityRepository.getSecurityItemById(securityId).first().password
                                if (realPassword == passwordFromUser) {
                                    action.postValue(SecurityStateContract.SuccessOnCheckPassword)
                                } else {
                                    action.postValue(SecurityStateContract.ErrorOnCheckPassword)
                                }
                            }
                        }
                    }

                    SecurityPlace.SECTION -> {

                    }
                }
            }

            is SecurityType.Create -> {
                when (type.securityPlace) {
                    SecurityPlace.TASK -> {
                        viewModelScope.launch {
                            if(insertedPassword == null) {
                                insertedPassword = passwordFromUser
                                action.value = SecurityStateContract.RepeatPassword
                            } else {
                                if(insertedPassword.equals(passwordFromUser)) {
                                    insertedPassword = null
                                    action.value = SecurityStateContract.SuccessOnRepeatPassword
                                } else {
                                    insertedPassword = null
                                    action.value = SecurityStateContract.ErrorOnRepeatPassword
                                }
                            }
                        }
                    }

                    SecurityPlace.SECTION -> {

                    }
                }
            }
        }
    }

    override fun onCleared() {
        insertedPassword = null
        super.onCleared()
    }
}
