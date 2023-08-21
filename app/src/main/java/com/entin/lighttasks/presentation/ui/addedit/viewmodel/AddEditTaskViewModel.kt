package com.entin.lighttasks.presentation.ui.addedit.viewmodel

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
class AddEditTaskViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: TasksRepository
) : ViewModel() {

    private val _editTaskChannel = Channel<EditTaskEvent>()
    val editTaskChannel = _editTaskChannel.receiveAsFlow()

    /**
     * If task is editing - task gotten from SavedStateHandle
     *
     */
    val task = state.get<Task>("task")

    private var taskPosition = ZERO

    init {
        /**
         * Setup position for new task = current max position + 1
         * On filling data -> save it to state
         */
        viewModelScope.launch(Dispatchers.IO) {
            taskPosition = repository.getMaxPosition().first()?.let { it + 1 } ?: ZERO
        }
    }

    var taskTitle = state.get<String>(TASK_TITLE) ?: task?.title ?: EMPTY_STRING
        set(value) {
            field = value
            state.set(TASK_TITLE, value)
        }

    var taskMessage = state.get<String>(TASK_MESSAGE) ?: task?.message ?: EMPTY_STRING
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

    fun saveTaskBtnClicked() = viewModelScope.launch(Dispatchers.IO) {
        if (taskTitle.isBlank()) {
            errorBlankText()
        } else {
            if (task != null) {
                val updatedTask = task.copy(
                    title = taskTitle,
                    message = taskMessage,
                    finished = taskFinished,
                    important = taskImportant,
                    group = taskGroup,
                    position = task.position,
                )
                updateTask(updatedTask)
            } else {
                val newTask = Task(
                    title = taskTitle,
                    message = taskMessage,
                    finished = taskFinished,
                    important = taskImportant,
                    group = taskGroup,
                    position = taskPosition
                )
                saveNewTask(newTask)
            }
        }
    }

    private fun errorBlankText() = viewModelScope.launch {
        _editTaskChannel.send(EditTaskEvent.ShowErrorBlankTitleText)
    }

    private fun updateTask(uTask: Task) = viewModelScope.launch {
        repository.updateTask(uTask)
        _editTaskChannel.send(EditTaskEvent.NavBackWithResult(TASK_EDIT))
    }

    private fun saveNewTask(sTask: Task) = viewModelScope.launch {
        repository.newTask(sTask)
        _editTaskChannel.send(EditTaskEvent.NavBackWithResult(TASK_NEW))
    }
}