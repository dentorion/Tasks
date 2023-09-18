package com.entin.lighttasks.presentation.ui.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.entity.IconTask
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.TASK
import com.entin.lighttasks.presentation.util.TASK_EDIT
import com.entin.lighttasks.presentation.util.TASK_EXPIRE_DATE_FIRST
import com.entin.lighttasks.presentation.util.TASK_EXPIRE_DATE_SECOND
import com.entin.lighttasks.presentation.util.TASK_FINISHED
import com.entin.lighttasks.presentation.util.TASK_GROUP
import com.entin.lighttasks.presentation.util.TASK_IMPORTANT
import com.entin.lighttasks.presentation.util.TASK_IS_EVENT
import com.entin.lighttasks.presentation.util.TASK_IS_EXPIRED
import com.entin.lighttasks.presentation.util.TASK_IS_RANGE
import com.entin.lighttasks.presentation.util.TASK_MESSAGE
import com.entin.lighttasks.presentation.util.TASK_NEW
import com.entin.lighttasks.presentation.util.TASK_TITLE
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.ZERO_LONG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
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

    private val _Icon_taskChannel = Channel<List<IconTask>>()
    val taskGroupsChannel = _Icon_taskChannel.receiveAsFlow()

    // If task is editing - task from SavedStateHandle
    val task: Task? = state.get<Task>(TASK)

    // Today
    private val defaultStartDateTime = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, ZERO)
        set(Calendar.MINUTE, ZERO)
    }.timeInMillis

    // Tomorrow
    private val defaultFinishDateTime = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
    }.timeInMillis + ONE_DAY_MLS

    init {
        /**
         * Get all groups for task to show icons
         */
        viewModelScope.launch(Dispatchers.IO) {
            _Icon_taskChannel.send(repository.getTaskIconGroups().shuffled())
        }
    }

    fun getTaskId(): Int? = task?.id

    var taskTitle = state.get<String>(TASK_TITLE) ?: task?.title ?: EMPTY_STRING
        set(value) {
            field = value.trim()
            state[TASK_TITLE] = value.trim()
        }

    var taskMessage = state.get<String>(TASK_MESSAGE) ?: task?.message ?: EMPTY_STRING
        set(value) {
            field = value.trim()
            state[TASK_MESSAGE] = value.trim()
        }

    var taskFinished = state.get<Boolean>(TASK_FINISHED) ?: task?.isFinished ?: false
        set(value) {
            field = value
            state[TASK_FINISHED] = value
        }

    var taskImportant = state.get<Boolean>(TASK_IMPORTANT) ?: task?.isImportant ?: false
        set(value) {
            field = value
            state[TASK_IMPORTANT] = value
        }

    var taskGroup: Int = state.get<Int>(TASK_GROUP) ?: task?.group ?: ZERO
        set(value) {
            field = value
            state[TASK_GROUP] = value
        }

    var taskExpireFirstDate: Long =
        state.get<Long>(TASK_EXPIRE_DATE_FIRST) ?: task?.expireDateFirst ?: defaultStartDateTime
        set(value) {
            field = value
            state[TASK_EXPIRE_DATE_FIRST] = value
        }

    var taskExpireSecondDate: Long =
        state.get<Long>(TASK_EXPIRE_DATE_SECOND) ?: task?.expireDateSecond ?: defaultFinishDateTime
        set(value) {
            field = value
            state[TASK_EXPIRE_DATE_SECOND] = value
        }

    var isEvent = state.get<Boolean>(TASK_IS_EVENT) ?: task?.isEvent ?: false
        set(value) {
            field = value
            state[TASK_IS_EVENT] = value
        }

    var isRange = state.get<Boolean>(TASK_IS_RANGE) ?: task?.isRange ?: true
        set(value) {
            field = value
            state[TASK_IS_RANGE] = value
        }

    var isTaskExpired = state.get<Boolean>(TASK_IS_EXPIRED) ?: task?.isTaskExpired ?: false
        set(value) {
            field = value
            state[TASK_IS_EXPIRED] = value
            if (value) {
                if (taskExpireFirstDate == ZERO_LONG) taskExpireFirstDate = defaultStartDateTime
                if (taskExpireSecondDate == ZERO_LONG) taskExpireSecondDate = defaultFinishDateTime
            }
        }

    fun saveTaskBtnClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            // First date > Second date in range task, that has date of expire
            if (isTaskExpired && isRange && taskExpireFirstDate > taskExpireSecondDate) {
                errorDatesPicked()
            } else {
                val expireDateFirst = if (isTaskExpired) taskExpireFirstDate else ZERO_LONG
                val expireDateSecond = if (isTaskExpired && isEvent) {
                    expireDateFirst
                } else if (isTaskExpired && isRange) {
                    taskExpireSecondDate
                } else {
                    ZERO_LONG
                }

                // Update
                if (task != null) {
                    updateTask(
                        task.copy(
                            id = task.id,
                            title = taskTitle,
                            message = taskMessage,
                            isFinished = taskFinished,
                            isImportant = taskImportant,
                            createdAt = task.createdAt,
                            editedAt = Date().time,
                            group = taskGroup,
                            position = task.position,
                            expireDateFirst = expireDateFirst,
                            expireDateSecond = expireDateSecond,
                            isTaskExpired = isTaskExpired,
                            isEvent = isEvent,
                            isRange = isRange,
                        ),
                    )
                }
                // Create new
                else {
                    saveNewTask(
                        Task(
                            id = ZERO, // will be replaced in Room
                            title = taskTitle,
                            message = taskMessage,
                            isFinished = taskFinished,
                            isImportant = taskImportant,
                            group = taskGroup,
                            position = ZERO, // will be replaced in Repository
                            createdAt = Date().time,
                            editedAt = ZERO_LONG,
                            expireDateFirst = expireDateFirst,
                            expireDateSecond = expireDateSecond,
                            isTaskExpired = isTaskExpired,
                            isEvent = isEvent,
                            isRange = isRange,
                        ),
                    )
                }
            }
        }
    }

    private suspend fun errorBlankText() {
        _editTaskChannel.send(EditTaskEventContract.ShowErrorBlankTitleText)
    }

    private suspend fun errorDatesPicked() {
        _editTaskChannel.send(EditTaskEventContract.ShowErrorDatesPicked)
    }

    private suspend fun updateTask(uTask: Task) {
        repository.updateTask(uTask).apply {
            if (this) _editTaskChannel.send(EditTaskEventContract.NavBackWithResult(TASK_EDIT))
        }
    }

    private suspend fun saveNewTask(task: Task) {
        repository.newTask(task).collect { result ->
            if (result) {
                _editTaskChannel.send(EditTaskEventContract.NavBackWithResult(TASK_NEW))
            } else {
                _editTaskChannel.send(EditTaskEventContract.ShowErrorBlankTitleText)
            }
        }
    }

    companion object {
        const val ONE_DAY_MLS = 86400000L
    }
}
