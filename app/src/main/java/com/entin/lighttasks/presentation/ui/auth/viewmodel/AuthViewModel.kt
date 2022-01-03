package com.entin.lighttasks.presentation.ui.auth.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.R
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.ui.addedit.contract.EditTaskEvent
import com.entin.lighttasks.presentation.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * ViewModel for AddEditTaskFragment
 */

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: TasksRepository
) : ViewModel() {


}