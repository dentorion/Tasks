package com.entin.lighttasks.presentation.screens.dialogs.security

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Security dialog
 */

@HiltViewModel
class SecurityDialogViewModel @Inject constructor(
    val state: SavedStateHandle,
) : ViewModel() {

    val action: MutableLiveData<SecurityStateContract> by lazy {
        MutableLiveData<SecurityStateContract>()
    }

    private var insertedPassword: String? = null

    fun onSubmitClicked(type: SecurityType, password: String) {
        when (type) {
            is SecurityType.Check -> {
                when (type.securityPlace) {
                    SecurityPlace.TASK -> {

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
                                insertedPassword = password
                                action.value = SecurityStateContract.RepeatPassword
                            } else {
                                if(insertedPassword.equals(password)) {
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
