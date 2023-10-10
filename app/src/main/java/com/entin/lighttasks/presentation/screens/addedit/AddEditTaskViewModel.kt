package com.entin.lighttasks.presentation.screens.addedit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.data.util.alarm.AlarmScheduler
import com.entin.lighttasks.domain.entity.AlarmItem
import com.entin.lighttasks.domain.entity.IconTask
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.repository.SectionsRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.IS_ALARM
import com.entin.lighttasks.presentation.util.LAST_HOUR
import com.entin.lighttasks.presentation.util.LAST_MINUTE
import com.entin.lighttasks.presentation.util.LAST_SECOND
import com.entin.lighttasks.presentation.util.LINK_ATTACHED
import com.entin.lighttasks.presentation.util.ONE
import com.entin.lighttasks.presentation.util.PHOTO_ATTACHED
import com.entin.lighttasks.presentation.util.SECTION
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
import com.entin.lighttasks.presentation.util.VOICE_ATTACHED
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.ZERO_LONG
import com.entin.lighttasks.presentation.util.getTimeMls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject

/**
 * ViewModel for AddEditTaskFragment
 */

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    val state: SavedStateHandle,
    private val taskRepository: TasksRepository,
    private val sectionsRepository: SectionsRepository,
) : ViewModel() {

    private val _editTaskChannel = Channel<EditTaskEventContract>()
    val editTaskChannel = _editTaskChannel.receiveAsFlow()

    private val _iconTaskChannel = Channel<List<IconTask>>()
    val iconTaskChannel = _iconTaskChannel.receiveAsFlow()

    // If task is editing - task from SavedStateHandle
    val task: Task? = state.get<Task>(TASK)

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

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

    var linkAttached = state.get<String>(LINK_ATTACHED) ?: task?.attachedLink ?: EMPTY_STRING
        set(value) {
            viewModelScope.launch {
                _editTaskChannel.send(
                    EditTaskEventContract.RefreshTagsVisibility(
                        url = value.isNotEmpty(),
                        photo = photoAttached.isNotEmpty(),
                        voice = voiceAttached.isNotEmpty(),
                    )
                )
                field = value.trim()
                state[LINK_ATTACHED] = value.trim()
            }
        }

    var photoAttached = state.get<String>(PHOTO_ATTACHED) ?: task?.attachedPhoto ?: EMPTY_STRING
        set(value) {
            viewModelScope.launch {
                _editTaskChannel.send(
                    EditTaskEventContract.RefreshTagsVisibility(
                        url = linkAttached.isNotEmpty(),
                        photo = value.isNotEmpty(),
                        voice = voiceAttached.isNotEmpty(),
                    )
                )
                field = value
                state[PHOTO_ATTACHED] = value
            }
        }

    var voiceAttached = state.get<String>(VOICE_ATTACHED) ?: task?.attachedVoice ?: EMPTY_STRING
        set(value) {
            viewModelScope.launch {
                _editTaskChannel.send(
                    EditTaskEventContract.RefreshTagsVisibility(
                        url = linkAttached.isNotEmpty(),
                        photo = photoAttached.isNotEmpty(),
                        voice = value.isNotEmpty(),
                    )
                )
                field = value
                state[VOICE_ATTACHED] = value
            }
        }
    var sectionId: Int = state.get<Int>(SECTION) ?: task?.sectionId ?: ZERO
        set(value) {
            field = value
            state[SECTION] = value
        }
    val sectionName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    var taskAlarm = state.get<Long>(IS_ALARM) ?: task?.taskAlarm ?: ZERO_LONG
        set(value) {
            field = value
            state[IS_ALARM] = value
        }
    var alarmIsOn: Boolean = taskAlarm != ZERO_LONG

    fun getTaskId(): Int? = task?.id

    /** Get all groups for task to show icons */
    fun getIcons() {
        viewModelScope.launch(Dispatchers.IO) {
            _iconTaskChannel.send(taskRepository.getTaskIconGroups().shuffled())
        }
    }

    fun getSectionById() {
        if(sectionId != ZERO) {
            viewModelScope.launch(Dispatchers.IO) {
                val name = sectionsRepository.getSectionById(sectionId).title
                sectionName.postValue(name)
            }
        } else {
            sectionName.value = EMPTY_STRING
        }
    }

    fun saveTaskBtnClicked() {
        viewModelScope.launch(Dispatchers.IO) {

            // Error: First date > Second date in range task, that has date of expire
            if (isTaskExpired && isRange && taskExpireFirstDate > taskExpireSecondDate) {
                errorDatesPicked()
            }
            // Error: Title and message are empty
            else if (taskTitle.isEmpty() && taskMessage.isEmpty()) {
                errorBlankText()
            }
            // Success: save task
            else {
                val expireDateFirst = if (isTaskExpired) taskExpireFirstDate else ZERO_LONG
                val expireDateSecond = if (isTaskExpired && isEvent) {
                    expireDateFirst
                } else if (isTaskExpired && isRange) {
                    taskExpireSecondDate
                } else {
                    ZERO_LONG
                }
                val alarm = if (alarmIsOn) {
                    if (taskAlarm != ZERO_LONG && (taskAlarm*1000) > Date().time) {
                        setAlarm()
                    }
                    taskAlarm
                } else {
                    if (taskAlarm != ZERO_LONG && (taskAlarm*1000) > Date().time) {
                        cancelAlarm()
                    }
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
                            attachedLink = linkAttached,
                            attachedPhoto = photoAttached,
                            attachedVoice = voiceAttached,
                            sectionId = sectionId,
                            taskAlarm = alarm,
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
                            attachedLink = linkAttached,
                            attachedPhoto = photoAttached,
                            attachedVoice = voiceAttached,
                            sectionId = sectionId,
                            taskAlarm = alarm,
                        ),
                    )
                }
            }
        }
    }

    private suspend fun errorBlankText() {
        _editTaskChannel.send(EditTaskEventContract.ShowErrorBlankTitleAndMessage)
    }

    private suspend fun errorDatesPicked() {
        _editTaskChannel.send(EditTaskEventContract.ShowErrorDatesPicked)
    }

    private suspend fun updateTask(uTask: Task) {
        taskRepository.updateTask(uTask).apply {
            state[TASK] = null
            if (this) _editTaskChannel.send(EditTaskEventContract.NavBackWithResult(TASK_EDIT))
        }
    }

    private suspend fun saveNewTask(task: Task) {
        taskRepository.newTask(task).collect { result ->
            state[TASK] = null
            if (result) {
                _editTaskChannel.send(EditTaskEventContract.NavBackWithResult(TASK_NEW))
            } else {
                _editTaskChannel.send(EditTaskEventContract.TaskNotSaved)
            }
        }
    }

    private fun setAlarm() {
        val localDateTimeOfAlarm = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(taskAlarm),
            TimeZone.getDefault().toZoneId()
        )
        alarmScheduler.schedule(
            AlarmItem(
                time = localDateTimeOfAlarm,
                message = "Alarm for task: $taskTitle",
                taskId = 1
            )
        )
    }

    private fun cancelAlarm() {
        val localDateTimeOfAlarm = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(taskAlarm),
            TimeZone.getDefault().toZoneId()
        )
        alarmScheduler.cancel(
            AlarmItem(
                time = localDateTimeOfAlarm,
                message = "Stop alarm for task: $taskTitle",
                taskId = 1
            )
        )
    }

    companion object {
        const val ONE_DAY_MLS = 86400000L
    }
}
