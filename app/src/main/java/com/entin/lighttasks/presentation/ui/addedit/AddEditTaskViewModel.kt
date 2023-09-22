package com.entin.lighttasks.presentation.ui.addedit

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.entity.IconTask
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.LAST_HOUR
import com.entin.lighttasks.presentation.util.LAST_MINUTE
import com.entin.lighttasks.presentation.util.LAST_SECOND
import com.entin.lighttasks.presentation.util.ONE
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
import com.entin.lighttasks.presentation.util.getCurrentDay
import com.entin.lighttasks.presentation.util.getCurrentMonth
import com.entin.lighttasks.presentation.util.getCurrentYear
import com.entin.lighttasks.presentation.util.getFinishDate
import com.entin.lighttasks.presentation.util.getTimeMls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for AddEditTaskFragment
 */

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val taskRepository: TasksRepository,
) : ViewModel() {


    private val _editTaskChannel = Channel<EditTaskEventContract>()
    val editTaskChannel = _editTaskChannel.receiveAsFlow()

    private val _iconTaskChannel = Channel<List<IconTask>>()
    val iconTaskChannel = _iconTaskChannel.receiveAsFlow()

    // If task is editing - task from SavedStateHandle
    val task: Task? = state.get<Task>(TASK)

    // Start date
    private val defaultStartDateTime: Long = getTimeMls(
        hours = ZERO,
        minutes = ONE,
        seconds = ONE,
    )

    // Finish date
    val defaultFinishDateTime: Long = getTimeMls(
        hours = LAST_HOUR,
        minutes = LAST_MINUTE,
        seconds = LAST_SECOND - ONE,
    ) + ONE_DAY_MLS

    init {
        /** Get all groups for task to show icons */
        viewModelScope.launch(Dispatchers.IO) {
            _iconTaskChannel.send(taskRepository.getTaskIconGroups().shuffled())
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
                Log.e("GlobalErrors", "taskExpireFirstDate: $taskExpireFirstDate, taskExpireSecondDate: $taskExpireSecondDate")
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
                            editedAt = getTimeMls(),
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
                            createdAt = getTimeMls(),
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
        taskRepository.updateTask(uTask).apply {
            if (this) _editTaskChannel.send(EditTaskEventContract.NavBackWithResult(TASK_EDIT))
        }
    }

    private suspend fun saveNewTask(task: Task) {
        taskRepository.newTask(task).collect { result ->
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
