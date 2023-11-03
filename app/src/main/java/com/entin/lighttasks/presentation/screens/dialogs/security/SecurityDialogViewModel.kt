package com.entin.lighttasks.presentation.screens.dialogs.security

import android.util.Log
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
            is Security.Check -> checkPassword(passwordFromUser, security.place)
            is Security.Create -> createPassword(passwordFromUser, security.place)
        }
    }

    /**
     * Check password
     */
    private fun checkPassword(passwordFromUser: String, securityType: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            when (securityType) {
                // Search is password set to Task
                is Place.TaskPlace -> securityRepository.getSecurityItemByTaskId(securityType.taskId).first()
                // Search is password set to Section
                is Place.SectionPlace -> securityRepository.getSecurityItemBySectionId(securityType.sectionId).first()
            }?.let { securityEntity: SecurityEntity ->
                // Password exists and can be checked
                if (securityEntity.password == passwordFromUser) {
                    action.postValue(SecurityStateContract.SuccessOnCheckPassword)
                } else {
                    action.postValue(SecurityStateContract.ErrorOnCheckPassword)
                }
            } ?: action.postValue(SecurityStateContract.ErrorNotFoundSecurityItemToCheckPassword)
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
                    selectUpdateOrCreatePassword(passwordFromUser, place)
                } else {
                    action.postValue(SecurityStateContract.ErrorOnRepeatPassword)
                }
                insertedPassword = null
            }
        }
    }

    /**
     * Password fot Section creating on existing section,
     * for task password should be saved / updated only if SAVE btn clicked.
     */
    private suspend fun selectUpdateOrCreatePassword(
        passwordFromUser: String,
        place: Place
    ) {
        Log.e("SECURITY_DIALOG", "selectUpdateOrCreatePassword()")
        when (place) {
            is Place.SectionPlace -> {
                Log.e("SECURITY_DIALOG", "selectUpdateOrCreatePassword(). PLACE")
                securityRepository.getSecurityItemBySectionId(place.sectionId).first()?.let {
                    updatePasswordDb(passwordFromUser, place) // Update password here
                } ?: insertPasswordDb(passwordFromUser, place) // Create password here
            }

            is Place.TaskPlace -> {
                Log.e("SECURITY_DIALOG", "selectUpdateOrCreatePassword(). TASK")
                securityRepository.getSecurityItemByTaskId(place.taskId).first()?.let {
                    action.postValue(SecurityStateContract.UpdateTaskPassword(passwordFromUser)) // Update password on save task
                } ?: action.postValue(SecurityStateContract.CreateTaskPassword(passwordFromUser)) // Create password on sae task
            }
        }
    }

    /** Update existing password */
    private suspend fun updatePasswordDb(passwordFromUser: String, place: Place) {
        when (place) {
            is Place.SectionPlace -> {
                securityRepository.updateSecurityItemBySectionId(
                    password = passwordFromUser, sectionId = place.sectionId
                )
            }

            is Place.TaskPlace -> {
                securityRepository.updateSecurityItemByTaskId(
                    password = passwordFromUser, taskId = place.taskId
                )
            }
        }
        successPasswordUpdate()
    }

    /** Add new password */
    private suspend fun insertPasswordDb(passwordFromUser: String, place: Place) {
        when (place) {
            is Place.SectionPlace -> {
                securityRepository.addSecurityItem(
                    SecurityEntity(
                        password = passwordFromUser,
                        taskId = ZERO,
                        sectionId = place.sectionId
                    )
                )
            }

            is Place.TaskPlace -> {
                securityRepository.addSecurityItem(
                    SecurityEntity(
                        password = passwordFromUser,
                        taskId = place.taskId,
                        sectionId = ZERO
                    )
                )
            }
        }
        successPasswordCreate()
    }

    /** Send success result of password update to Security dialog */
    private fun successPasswordUpdate() {
        action.postValue(SecurityStateContract.SuccessOnUpdatePassword)
    }

    /** Send success result of password create to Security dialog */
    private fun successPasswordCreate() {
        action.postValue(SecurityStateContract.SuccessOnCreatePassword)
    }

    override fun onCleared() {
        insertedPassword = null
        super.onCleared()
    }
}
