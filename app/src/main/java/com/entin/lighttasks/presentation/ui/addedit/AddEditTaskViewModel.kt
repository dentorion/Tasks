package com.entin.lighttasks.presentation.ui.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.entity.TaskGroup
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.TASK_EDIT
import com.entin.lighttasks.presentation.util.TASK_FINISHED
import com.entin.lighttasks.presentation.util.TASK_GROUP
import com.entin.lighttasks.presentation.util.TASK_IMPORTANT
import com.entin.lighttasks.presentation.util.TASK_MESSAGE
import com.entin.lighttasks.presentation.util.TASK_NEW
import com.entin.lighttasks.presentation.util.TASK_TITLE
import com.entin.lighttasks.presentation.util.ZERO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for AddEditTaskFragment
 */

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: TasksRepository,
) : ViewModel() {

    private val _editTaskChannel = Channel<EditTaskEventContract>()
    val editTaskChannel = _editTaskChannel.receiveAsFlow()

    private val _taskGroupChannel = Channel<List<TaskGroup>>()
    val taskGroupsChannel = _taskGroupChannel.receiveAsFlow()

    /**
     * If task is editing - task gotten from SavedStateHandle
     *
     */
    val task = state.get<Task>("task")
    private var taskPosition = ZERO
    private var taskGroups = listOf<TaskGroup>()

    init {
        /**
         * Setup position for new task = current max position + 1
         * On filling data -> save it to state
         */
        viewModelScope.launch(Dispatchers.IO) {
            taskPosition = repository.getMaxPosition().first()?.let { it + 1 } ?: ZERO
        }

        /**
         * Get all groups for task to show icons
         */
        viewModelScope.launch(Dispatchers.IO) {
            _taskGroupChannel.send(repository.getTaskGroups())
        }
    }

    var taskTitle = state.get<String>(TASK_TITLE) ?: task?.title ?: EMPTY_STRING
        set(value) {
            field = value
            state[TASK_TITLE] = value
        }

    var taskMessage = state.get<String>(TASK_MESSAGE) ?: task?.message ?: EMPTY_STRING
        set(value) {
            field = value
            state[TASK_MESSAGE] = value
        }

    var taskFinished = state.get<Boolean>(TASK_FINISHED) ?: task?.finished ?: false
        set(value) {
            field = value
            state[TASK_FINISHED] = value
        }

    var taskImportant = state.get<Boolean>(TASK_IMPORTANT) ?: task?.important ?: false
        set(value) {
            field = value
            state[TASK_IMPORTANT] = value
        }

    var taskGroup: Int = state.get<Int>(TASK_GROUP) ?: task?.group ?: ZERO
        set(value) {
            field = value
            state[TASK_GROUP] = value
        }

    fun saveTaskBtnClicked() = viewModelScope.launch(Dispatchers.IO) {
        if (taskTitle.isBlank()) {
            errorBlankText()
        } else {
            if (task != null) {
                updateTask(
                    task.copy(
                        title = taskTitle,
                        message = taskMessage,
                        finished = taskFinished,
                        important = taskImportant,
                        group = taskGroup,
                        position = task.position,
                    ),
                )
            } else {
                saveNewTask(
                    Task(
                        title = taskTitle,
                        message = taskMessage,
                        finished = taskFinished,
                        important = taskImportant,
                        group = taskGroup,
                        position = taskPosition,
                    ),
                )
            }
        }
    }

    private fun errorBlankText() = viewModelScope.launch {
        _editTaskChannel.send(EditTaskEventContract.ShowErrorBlankTitleText)
    }

    private fun updateTask(uTask: Task) = viewModelScope.launch {
        repository.updateTask(uTask).apply {
            if (this) _editTaskChannel.send(EditTaskEventContract.NavBackWithResult(TASK_EDIT))
        }
    }

    private fun saveNewTask(sTask: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.newTask(sTask).collect { result ->
            if (result) {
                _editTaskChannel.send(EditTaskEventContract.NavBackWithResult(TASK_NEW))
            } else {
                _editTaskChannel.send(EditTaskEventContract.ShowErrorBlankTitleText)
            }
        }
    }
}
