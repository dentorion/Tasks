package com.entin.lighttasks.presentation.screens.dialogs.security

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
    ) {
        when (securityType) {
            is SecurityType.Check -> {
                when (securityType.securityPlace) {
                    is SecurityPlace.TaskPlace -> checkPasswordCode(
                        passwordFromUser,
                        securityType.securityPlace
                    )

                    is SecurityPlace.SectionPlace -> checkPasswordCode(
                        passwordFromUser,
                        securityType.securityPlace
                    )
                }
            }

            is SecurityType.Create -> {
                when (securityType.securityPlace) {
                    is SecurityPlace.TaskPlace -> createPasswordCode(passwordFromUser)

                    is SecurityPlace.SectionPlace -> createPasswordCode(passwordFromUser)
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

    private fun checkPasswordCode(passwordFromUser: String, securityType: SecurityPlace) {
        viewModelScope.launch(Dispatchers.IO) {
            when (securityType) {
                // Task
                is SecurityPlace.TaskPlace -> {
                    securityType.task?.id?.let {
                        securityRepository.getSecurityItemByTaskId(it).first()?.id
                    }
                }
                // Section
                is SecurityPlace.SectionPlace -> {
                    securityType.sectionId?.let {
                        securityRepository.getSecurityItemBySectionId(it).first()?.id
                    }
                }
            }.also { securityId: Int? ->
                securityId?.let {
                    securityRepository.getSecurityItemById(securityId)
                        .first()
                        .password
                        .also { realPassword: String ->
                            if (realPassword == passwordFromUser) {
                                action.postValue(SecurityStateContract.SuccessOnCheckPassword)
                            } else {
                                action.postValue(SecurityStateContract.ErrorOnCheckPassword)
                            }
                        }
                } ?: kotlin.run { action.postValue(SecurityStateContract.NotFoundSecurityItem) }
            }
        }
    }

    override fun onCleared() {
        insertedPassword = null
        super.onCleared()
    }
}
