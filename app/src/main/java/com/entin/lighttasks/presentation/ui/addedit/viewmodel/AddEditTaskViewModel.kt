package com.entin.lighttasks.presentation.ui.addedit.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.R
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.repository.RemoteTasksRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.ui.addedit.contract.EditTaskEvent
import com.entin.lighttasks.presentation.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * ViewModel for AddEditTaskFragment
 */

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: TasksRepository,
    private val remoteRepository: RemoteTasksRepository,
) : ViewModel() {

    private val _editTaskChannel = Channel<EditTaskEvent>()
    val editTaskChannel = _editTaskChannel.receiveAsFlow()

    /**
     * If task is editing - task gotten from SavedStateHandle
     *
     */
    val task = state.get<Task>("task")

    /**
     * New value position of Task
     */
    private var taskPosition by Delegates.notNull<Int>()

    /**
     * Creation time value
     * Purpose is to have the same value of creation time in Remote Task and local Task.
     */
    private var creationDate by Delegates.notNull<Long>()

    init {
        /**
         * Setup position for new task = current max position + 1
         * On filling data -> save it to state
         */
        viewModelScope.launch(Dispatchers.IO) {
            taskPosition = repository.getMaxPosition().first() + 1
        }

        /**
         * Setup value of creationDate
         */
        creationDate = task?.date ?: System.currentTimeMillis()
    }

    var taskTitle = state.get<String>(TASK_TITLE) ?: task?.title ?: ""
        set(value) {
            field = value
            state.set(TASK_TITLE, value)
        }

    var taskMessage = state.get<String>(TASK_MESSAGE) ?: task?.message ?: ""
        set(value) {
            field = value
            state.set(TASK_MESSAGE, value)
        }

    var taskFinished = state.get<Boolean>(TASK_FINISHED) ?: task?.finished ?: false
        set(value) {
            field = value
            state.set(TASK_FINISHED, value)
        }

    var taskImportant = state.get<Boolean>(TASK_IMPORTANT) ?: task?.important ?: false
        set(value) {
            field = value
            state.set(TASK_IMPORTANT, value)
        }

    var taskGroup: Int = state.get<Int>(TASK_GROUP) ?: task?.group ?: R.id.radio_empty
        set(value) {
            field = value
            state.set(TASK_GROUP, value)
        }

    /**
     * Local save button reaction
     */
    fun saveTaskBtnClicked() = viewModelScope.launch {
        if (taskTitle.isBlank()) {
            errorBlankText()
        } else {
            if (task != null) {
                task.copy(
                    title = taskTitle,
                    message = taskMessage,
                    finished = taskFinished,
                    important = taskImportant,
                    group = taskGroup,
                    position = task.position,
                    date = creationDate,
                ).also { updatedTask ->
                    updateTask(updatedTask)
                }
            } else {
                Task(
                    title = taskTitle,
                    message = taskMessage,
                    finished = taskFinished,
                    important = taskImportant,
                    group = taskGroup,
                    position = taskPosition,
                    date = creationDate,
                ).also { task ->
                    saveNewTask(task)
                }
            }
        }
    }

    /**
     * Cloud save button reaction
     */
    fun saveTaskToRemote() = viewModelScope.launch {
        if(taskTitle.isBlank().not()) {
            Task(
                title = taskTitle,
                message = taskMessage,
                finished = taskFinished,
                important = taskImportant,
                group = taskGroup,
                position = 0,
                date = creationDate,
            ).also { task ->
                remoteRepository.saveSingleTaskToRemote(task).collect { result ->
                    _editTaskChannel.send(EditTaskEvent.SaveTaskToRemoteSuccess(result))
                }
            }
        } else {
            _editTaskChannel.send(EditTaskEvent.SaveTaskToRemoteSuccess(false))
        }
    }

    // UTIL FUNCTIONS

    /**
     * Check title is not empty
     */
    private fun errorBlankText() = viewModelScope.launch {
        _editTaskChannel.send(EditTaskEvent.ShowErrorBlankTitleText)
    }

    /**
     * Update existing task to local storage
     */
    private fun updateTask(uTask: Task) = viewModelScope.launch {
        repository.updateTask(uTask)
        _editTaskChannel.send(EditTaskEvent.NavBackWithResult(TASK_EDIT))
    }

    /**
     * Save new task to local storage
     */
    private fun saveNewTask(sTask: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.newTask(sTask).collect { result ->
            if (result) {
                _editTaskChannel.send(EditTaskEvent.NavBackWithResult(TASK_NEW))
            } else {
                _editTaskChannel.send(EditTaskEvent.NavBackWithResult(TASK_EXIST))
            }
        }
    }
}