package com.entin.lighttasks.presentation.screens.dialogs.security

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.data.db.entity.SecurityEntity
import com.entin.lighttasks.domain.repository.SecurityRepository
import com.entin.lighttasks.presentation.util.ZERO
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
        security: Security,
        passwordFromUser: String,
    ) {
        when (security) {
            is Security.Check -> {
                when (security.place) {
                    is Place.TaskPlace -> checkPassword(
                        passwordFromUser,
                        security.place
                    )

                    is Place.SectionPlace -> checkPassword(
                        passwordFromUser,
                        security.place
                    )
                }
            }

            is Security.Create -> {
                when (security.place) {
                    is Place.TaskPlace -> createPassword(
                        passwordFromUser,
                        security.place
                    )

                    is Place.SectionPlace -> createPassword(
                        passwordFromUser,
                        security.place
                    )
                }
            }
        }
    }

    /**
     * Create password
     */
    private fun createPassword(passwordFromUser: String, place: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            if (insertedPassword == null) {
                insertedPassword = passwordFromUser
                action.postValue(SecurityStateContract.RepeatPassword)
            } else {
                if (insertedPassword.equals(passwordFromUser)) {
                    insertedPassword = null
                    selectUpdateOrCreatePassword(passwordFromUser, place)
                } else {
                    insertedPassword = null
                    action.postValue(SecurityStateContract.ErrorOnRepeatPassword)
                }
            }
        }
    }

    private suspend fun selectUpdateOrCreatePassword(
        passwordFromUser: String,
        place: Place
    ) {
        when (place) {
            is Place.SectionPlace -> {
                place.sectionId?.let { sectionId ->
                    securityRepository.getSecurityItemBySectionId(sectionId).first()?.let {
                        // Update password
                        updatePasswordDb(passwordFromUser, place)
                    } ?: kotlin.run {
                        // Create password
                        insertPasswordDb(passwordFromUser, place)
                    }
                } ?: kotlin.run {
                    // Create password
                    insertPasswordDb(passwordFromUser, place)
                }
            }

            is Place.TaskPlace -> {
                place.taskId?.let { taskId ->
                    securityRepository.getSecurityItemByTaskId(taskId).first()?.let {
                        // Update password
                        updatePasswordDb(passwordFromUser, place)
                    } ?: kotlin.run {
                        // Create password
                        insertPasswordDb(passwordFromUser, place)
                    }
                } ?: kotlin.run {
                    // Create password
                    insertPasswordDb(passwordFromUser, place)
                }
            }
        }
    }

    private suspend fun updatePasswordDb(passwordFromUser: String, place: Place) {
        when (place) {
            is Place.SectionPlace -> {
                place.sectionId?.let { sectionId ->
                    securityRepository.updateSecurityItemBySectionId(
                        password = passwordFromUser,
                        sectionId = sectionId
                    )
                    successPasswordUpdate()
                } ?: kotlin.run {
                    action.postValue(SecurityStateContract.ErrorSectionIdIsNull(127))
                    successPasswordUpdate()
                }
            }

            is Place.TaskPlace -> {
                place.taskId?.let { taskId ->
                    securityRepository.updateSecurityItemByTaskId(
                        password = passwordFromUser,
                        taskId = taskId,
                    )
                    successPasswordUpdate()
                } ?: kotlin.run {
                    action.postValue(SecurityStateContract.ErrorTaskIdIsNull(137))
                    successPasswordUpdate()
                }
            }
        }
    }

    private suspend fun insertPasswordDb(passwordFromUser: String, place: Place) {
        when (place) {
            is Place.SectionPlace -> {
                place.sectionId?.let { sectionId ->
                    securityRepository.addSecurityItem(
                        SecurityEntity(
                            password = passwordFromUser,
                            taskId = ZERO,
                            sectionId = sectionId
                        )
                    )
                    successPasswordCreate()
                } ?: kotlin.run { action.postValue(SecurityStateContract.ErrorSectionIdIsNull(154)) }
            }

            is Place.TaskPlace -> {
                place.taskId?.let { taskId ->
                    securityRepository.addSecurityItem(
                        SecurityEntity(
                            password = passwordFromUser,
                            taskId = taskId,
                            sectionId = ZERO
                        )
                    )
                    successPasswordCreate()
                } ?: kotlin.run { action.postValue(SecurityStateContract.ErrorTaskIdIsNull(167)) }
            }
        }
    }

    private fun successPasswordUpdate() {
        action.postValue(SecurityStateContract.SuccessOnUpdatePassword)
    }

    private fun successPasswordCreate() {
        action.postValue(SecurityStateContract.SuccessOnCreatePassword)
    }

    /**
     * Check password
     */
    private fun checkPassword(passwordFromUser: String, securityType: Place) {
        var purpose: SecurityPurpose? = null
        viewModelScope.launch(Dispatchers.IO) {
            when (securityType) {
                // Task
                is Place.TaskPlace -> {
                    securityType.taskId?.let {
                        securityRepository.getSecurityItemByTaskId(it).first()?.id
                    }
                }
                // Section
                is Place.SectionPlace -> {
                    securityType.sectionId?.let {
                        securityRepository.getSecurityItemBySectionId(it).first()?.id
                    }
                }
            }.also { securityId: Int? ->
                securityId?.let {
                    // Task has password and can be checked
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
                } ?: kotlin.run {
                    action.postValue(SecurityStateContract.ErrorNotFoundSecurityItem(211))
                }
            }
        }
    }

    override fun onCleared() {
        insertedPassword = null
        super.onCleared()
    }
}
