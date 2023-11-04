package com.entin.lighttasks.presentation.screens.addedit

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.data.db.entity.AlarmItemEntity
import com.entin.lighttasks.data.db.entity.IconTaskEntity
import com.entin.lighttasks.data.db.entity.SecurityEntity
import com.entin.lighttasks.data.db.entity.TaskEntity
import com.entin.lighttasks.data.util.alarm.AlarmScheduler
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.repository.AlarmsRepository
import com.entin.lighttasks.domain.repository.SectionsRepository
import com.entin.lighttasks.domain.repository.SecurityRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.GALLERY_PICKED_IMAGES
import com.entin.lighttasks.presentation.util.IS_ALARM
import com.entin.lighttasks.presentation.util.IS_PASSWORD_CREATION
import com.entin.lighttasks.presentation.util.IS_PASSWORD_TURN_ON
import com.entin.lighttasks.presentation.util.LINK_ATTACHED
import com.entin.lighttasks.presentation.util.PHOTO_ATTACHED
import com.entin.lighttasks.presentation.util.SECTION
import com.entin.lighttasks.presentation.util.TASK
import com.entin.lighttasks.presentation.util.TASK_ALARM
import com.entin.lighttasks.presentation.util.TASK_EDIT
import com.entin.lighttasks.presentation.util.TASK_EXPIRE_DATE_FIRST
import com.entin.lighttasks.presentation.util.TASK_EXPIRE_DATE_SECOND
import com.entin.lighttasks.presentation.util.TASK_FINISHED
import com.entin.lighttasks.presentation.util.TASK_HAS_PASSWORD
import com.entin.lighttasks.presentation.util.TASK_ICON
import com.entin.lighttasks.presentation.util.TASK_IMPORTANT
import com.entin.lighttasks.presentation.util.TASK_IS_EVENT
import com.entin.lighttasks.presentation.util.TASK_IS_EXPIRED
import com.entin.lighttasks.presentation.util.TASK_IS_RANGE
import com.entin.lighttasks.presentation.util.TASK_MESSAGE
import com.entin.lighttasks.presentation.util.TASK_NEW
import com.entin.lighttasks.presentation.util.TASK_NEW_PASSWORD
import com.entin.lighttasks.presentation.util.TASK_TITLE
import com.entin.lighttasks.presentation.util.VOICE_ATTACHED
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.ZERO_LONG
import com.entin.lighttasks.presentation.util.getDefaultFinishDateTime
import com.entin.lighttasks.presentation.util.getDefaultStartDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
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
    private val securityRepository: SecurityRepository,
    private val sectionsRepository: SectionsRepository,
    private val alarmsRepository: AlarmsRepository,
) : ViewModel() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    private val _editTaskChannel = Channel<EditTaskEventContract>()
    val editTaskChannel = _editTaskChannel.receiveAsFlow()

    private val _iconTaskEntityChannel = Channel<List<IconTaskEntity>>()
    val iconTaskChannel = _iconTaskEntityChannel.receiveAsFlow()

    /** If task is editing - task from SavedStateHandle */
    val taskEntity: Task? = state.get<Task>(TASK)

    /** Get task id if exist */
    fun getTaskId(): Int? = taskEntity?.id

    /**
     * Get next task id
     */
    var nextTaskId: Int = Int.MIN_VALUE

    var taskTitle = state.get<String>(TASK_TITLE) ?: taskEntity?.title ?: EMPTY_STRING
        set(value) {
            field = value.trim()
            state[TASK_TITLE] = value.trim()
        }

    var taskMessage = state.get<String>(TASK_MESSAGE) ?: taskEntity?.message ?: EMPTY_STRING
        set(value) {
            field = value.trim()
            state[TASK_MESSAGE] = value.trim()
        }

    var taskFinished = state.get<Boolean>(TASK_FINISHED) ?: taskEntity?.isFinished ?: false
        set(value) {
            field = value
            state[TASK_FINISHED] = value
        }

    var taskImportant = state.get<Boolean>(TASK_IMPORTANT) ?: taskEntity?.isImportant ?: false
        set(value) {
            field = value
            state[TASK_IMPORTANT] = value
        }
    
    /** Icon of task */
    var taskIcon: Int = state.get<Int>(TASK_ICON) ?: taskEntity?.group ?: ZERO
        set(value) {
            field = value
            state[TASK_ICON] = value
        }

    /** Start and Finish dates for time limiting for task */

    var taskExpireFirstDate: Long =
        state.get<Long>(TASK_EXPIRE_DATE_FIRST) ?: taskEntity?.expireDateFirst
        ?: getDefaultStartDateTime()
        set(value) {
            field = value
            state[TASK_EXPIRE_DATE_FIRST] = value
        }

    var taskExpireSecondDate: Long =
        state.get<Long>(TASK_EXPIRE_DATE_SECOND) ?: taskEntity?.expireDateSecond
        ?: getDefaultFinishDateTime()
        set(value) {
            field = value
            state[TASK_EXPIRE_DATE_SECOND] = value
        }

    /** Task should be done in concrete day */

    var isEvent = state.get<Boolean>(TASK_IS_EVENT) ?: taskEntity?.isEvent ?: false
        set(value) {
            field = value
            state[TASK_IS_EVENT] = value
        }

    /** Task has a period of time to be done */

    var isRange = state.get<Boolean>(TASK_IS_RANGE) ?: taskEntity?.isRange ?: true
        set(value) {
            field = value
            state[TASK_IS_RANGE] = value
        }

    /** If the checkbox of time limit is on */

    var isTaskExpired = state.get<Boolean>(TASK_IS_EXPIRED) ?: taskEntity?.isTaskExpired ?: false
        set(value) {
            field = value
            state[TASK_IS_EXPIRED] = value
            if (value) {
                if (taskExpireFirstDate == ZERO_LONG) taskExpireFirstDate = getDefaultStartDateTime()
                if (taskExpireSecondDate == ZERO_LONG) taskExpireSecondDate = getDefaultFinishDateTime()
            }
        }

    /** Link attach */

    var linkAttached = state.get<String>(LINK_ATTACHED) ?: taskEntity?.attachedLink ?: EMPTY_STRING
        set(value) {
            viewModelScope.launch {
                field = value.trim()
                state[LINK_ATTACHED] = value.trim()
                _editTaskChannel.send(
                    EditTaskEventContract.RefreshTagsVisibility(
                        url = value.isNotEmpty(),
                        photo = photoAttached.isNotEmpty(),
                        voice = voiceAttached.isNotEmpty(),
                        galleryImages = attachedGalleryImages.isNotEmpty(),
                    )
                )
            }
        }

    /** Photo from camera attach */

    var photoAttached =
        state.get<String>(PHOTO_ATTACHED) ?: taskEntity?.attachedPhoto ?: EMPTY_STRING
        set(value) {
            viewModelScope.launch {
                field = value
                state[PHOTO_ATTACHED] = value
                _editTaskChannel.send(
                    EditTaskEventContract.RefreshTagsVisibility(
                        url = linkAttached.isNotEmpty(),
                        photo = value.isNotEmpty(),
                        voice = voiceAttached.isNotEmpty(),
                        galleryImages = attachedGalleryImages.isNotEmpty(),
                    )
                )
            }
        }

    /** Audio record attach */

    var voiceAttached =
        state.get<String>(VOICE_ATTACHED) ?: taskEntity?.attachedVoice ?: EMPTY_STRING
        set(value) {
            viewModelScope.launch {
                field = value
                state[VOICE_ATTACHED] = value
                _editTaskChannel.send(
                    EditTaskEventContract.RefreshTagsVisibility(
                        url = linkAttached.isNotEmpty(),
                        photo = photoAttached.isNotEmpty(),
                        voice = value.isNotEmpty(),
                        galleryImages = attachedGalleryImages.isNotEmpty(),
                    )
                )
            }
        }

    /** Picked images from gallery */

    var attachedGalleryImages: List<Uri> =
        state.get<List<Uri>>(GALLERY_PICKED_IMAGES) ?: taskEntity?.attachedGalleryImages ?: listOf()
        set(value) {
            viewModelScope.launch {
                field = value
                state[GALLERY_PICKED_IMAGES] = value
                _editTaskChannel.send(
                    EditTaskEventContract.RefreshTagsVisibility(
                        url = linkAttached.isNotEmpty(),
                        photo = photoAttached.isNotEmpty(),
                        voice = voiceAttached.isNotEmpty(),
                        galleryImages = value.isNotEmpty(),
                    )
                )
            }
        }

    /**  Section */

    var sectionId: Int = state.get<Int>(SECTION) ?: taskEntity?.sectionId ?: ZERO
        set(value) {
            field = value
            state[SECTION] = value
        }

    val sectionName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    /** Alarm */

    var taskAlarm = state.get<Long>(TASK_ALARM) ?: taskEntity?.alarmTime ?: ZERO_LONG
        set(value) {
            field = value
            state[TASK_ALARM] = value
        }

    var alarmIsOn: Boolean = (taskAlarm != ZERO_LONG)
        set(value) {
            field = value
            state[IS_ALARM] = value
        }

    private var alarmId: Long = ZERO_LONG

    /** Security */

    var hasPasswordOnStart =
        state.get<Boolean>(TASK_HAS_PASSWORD) ?: taskEntity?.hasPassword ?: false
        private set(value) {
            field = value
            state[TASK_HAS_PASSWORD] = value
        }

    var isPasswordCreation: Boolean = state.get<Boolean>(IS_PASSWORD_CREATION) ?: false
        set(value) {
            field = value
            state[IS_PASSWORD_CREATION] = value
        }

    var taskNewPassword: String = state.get<String>(TASK_NEW_PASSWORD) ?: EMPTY_STRING
        set(value) {
            field = value
            state[TASK_NEW_PASSWORD] = value
            viewModelScope.launch {
                _editTaskChannel.send(EditTaskEventContract.OnSuccessPasswordCreateOrUpdate)
            }
        }

    var isPasswordSecurityTurnOn: Boolean =
        state.get<Boolean>(IS_PASSWORD_TURN_ON) ?: hasPasswordOnStart
        set(value) {
            field = value
            state[IS_PASSWORD_TURN_ON] = value
            viewModelScope.launch {
                _editTaskChannel.send(EditTaskEventContract.OnSuccessPasswordCreateOrUpdate)
            }
        }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            nextTaskId = taskRepository.getNextTaskId().first()
        }
    }

    /** Get all icons */
    fun getTaskIcons() {
        viewModelScope.launch(Dispatchers.IO) {
            _iconTaskEntityChannel.send(
                taskRepository.getTaskIcons().shuffled()
            )
        }
    }

    /** Get section by id to show while creating or editing task */
    fun getSectionById() {
        if(sectionId != ZERO) {
            viewModelScope.launch(Dispatchers.IO) {
                val sectionTitle = sectionsRepository.getSectionById(sectionId).first().title
                sectionName.postValue(sectionTitle)
            }
        } else {
            sectionName.value = EMPTY_STRING
        }
    }

    /** OK button clicked while creating or editing task.
     *  Checks for errors and setup password, alarm
     */
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
            // Error: Alarm time is less or equal now
            else if (alarmIsOn && taskAlarm != ZERO_LONG && taskAlarm <= Date().time) {
                errorAlarmTime()
            }
            // Success: save task
            else {
                // Password
                passwordForTask()

                // Alarm
                setAlarmForTask()

                // Save task
                saveTask()
            }
        }
    }

    /** Save task */
    private suspend fun saveTask() {
        // Update
        if (taskEntity != null) {
            updateTask(
                TaskEntity(
                    id = taskEntity.id,
                    title = taskTitle,
                    message = taskMessage,
                    isFinished = taskFinished,
                    isImportant = taskImportant,
                    createdAt = taskEntity.createdAt,
                    editedAt = Date().time, // getTimeMls()
                    group = taskIcon,
                    position = taskEntity.position,
                    expireDateFirst = getFirstExpireDateForTask(),
                    expireDateSecond = getSecondExpireDateForTask(),
                    isTaskExpired = isTaskExpired,
                    isEvent = isEvent,
                    isRange = isRange,
                    attachedLink = linkAttached,
                    attachedPhoto = photoAttached,
                    attachedVoice = voiceAttached,
                    sectionId = sectionId,
                    alarmId = alarmId,
                    attachedGalleryImages = attachedGalleryImages
                )
            )
        }
        // Create new
        else {
            saveNewTask(
                TaskEntity(
                    id = ZERO, // will be replaced in Room
                    title = taskTitle,
                    message = taskMessage,
                    isFinished = taskFinished,
                    isImportant = taskImportant,
                    group = taskIcon,
                    position = ZERO, // will be replaced in Repository
                    createdAt = Date().time, // getTimeMls()
                    editedAt = ZERO_LONG,
                    expireDateFirst = getFirstExpireDateForTask(),
                    expireDateSecond = getSecondExpireDateForTask(),
                    isTaskExpired = isTaskExpired,
                    isEvent = isEvent,
                    isRange = isRange,
                    attachedLink = linkAttached,
                    attachedPhoto = photoAttached,
                    attachedVoice = voiceAttached,
                    sectionId = sectionId,
                    alarmId = alarmId,
                    attachedGalleryImages = attachedGalleryImages
                ),
            )
        }
    }

    /** Create new task implementation */
    private suspend fun saveNewTask(taskEntity: TaskEntity) {
        taskRepository.newTask(taskEntity).collect { result ->
            state[TASK] = null
            if (result) {
                _editTaskChannel.send(EditTaskEventContract.NavBackWithResult(TASK_NEW))
            } else {
                _editTaskChannel.send(EditTaskEventContract.TaskNotSaved)
            }
        }
    }

    /** Update task implementation */
    private suspend fun updateTask(taskEntity: TaskEntity) {
        taskRepository.updateTask(taskEntity).apply {
            state[TASK] = null
            if (this) _editTaskChannel.send(EditTaskEventContract.NavBackWithResult(TASK_EDIT))
        }
    }
    
    /** Set alarm for task while saving it */
    private suspend fun setAlarmForTask() {
        // checkBox = true and alarm time set
        if (alarmIsOn && taskAlarm != ZERO_LONG) {
            setAlarm()
        }
        // checkBox = true and alarm time not set
        else if (alarmIsOn && taskAlarm == ZERO_LONG) {

        }
        // checkBox = false and time not set
        else if (!alarmIsOn && taskAlarm == ZERO_LONG) {

        }
        // checkBox = false and alarm time set
        else if (!alarmIsOn && taskAlarm != ZERO_LONG) {
            cancelAlarm()
        }
    }

    /** Set alarm implementation */
    private suspend fun setAlarm() {
        // Set time for AlarmItem
        val localDateTimeOfAlarm = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(taskAlarm), TimeZone.getDefault().toZoneId()
        ).atZone(ZoneId.systemDefault()).toEpochSecond() * ONE_SEC_MLS

        // TaskId for AlarmItem (next id of task)
        val taskId = taskEntity?.id ?: taskRepository.getNextTaskId().first()

        // Create AlarmItem
        val alarmItemEntity = AlarmItemEntity(
            alarmTime = localDateTimeOfAlarm,
            alarmMessage = taskTitle,
            taskId = taskId
        )

        // Add alarm to Android
        alarmScheduler.schedule(alarmItemEntity)

        // Add alarm to database and set alarmId
        alarmId = alarmsRepository.addAlarm(alarmItemEntity)
    }

    /** Cancel alarm implementation */
    private fun cancelAlarm() {
        viewModelScope.launch(Dispatchers.IO) {
            taskEntity?.let {
                // Cancel alarm from Android
                alarmScheduler.cancel(
                    AlarmItemEntity(
                        alarmTime = ZERO_LONG,
                        alarmMessage = taskTitle,
                        taskId = it.id
                    )
                )

                // Delete alarm from database
                alarmsRepository.deleteAlarmByTaskId(it.id)

                // Clear alarmId
                alarmId = ZERO_LONG
            }
        }
    }

    /** Set password for task */
    private suspend fun passwordForTask() {
        Log.e("SECURITY_DIALOG", "passwordForTask()")

        // Was password and security switch off
        if (hasPasswordOnStart && !isPasswordSecurityTurnOn) {
            taskEntity?.id?.let { taskId ->
                securityRepository.deleteSecurityItemByTaskId(taskId)
            }
        }

        if (taskNewPassword != EMPTY_STRING && isPasswordSecurityTurnOn) {
            when (isPasswordCreation) {
                true -> securityRepository.addSecurityItem(
                    SecurityEntity(
                        password = taskNewPassword,
                        taskId = getTaskId() ?: nextTaskId,
                        sectionId = ZERO
                    )
                )
                false -> securityRepository.updateSecurityItemByTaskId(
                    taskId = getTaskId() ?: nextTaskId,
                    password = taskNewPassword
                )
            }
        }
    }
    
    /** Add to existing List of uri the List of uri of picked images from gallery to task  */
    fun addListUriOfGalleryImages(listUri: List<Uri>) {
        attachedGalleryImages = attachedGalleryImages.toMutableList().apply {
            this.addAll(listUri)
        }.toList()
    }
    
    /** Dates that describe periods of time for task to be done. While saving task */
    
    private fun getFirstExpireDateForTask(): Long =
        if (isTaskExpired) taskExpireFirstDate else ZERO_LONG

    private fun getSecondExpireDateForTask(): Long =
        if (isTaskExpired && isEvent) {
            getFirstExpireDateForTask()
        } else if (isTaskExpired && isRange) {
            taskExpireSecondDate
        } else {
            ZERO_LONG
        }

    /** Error to show: blank title */
    private suspend fun errorBlankText() {
        _editTaskChannel.send(EditTaskEventContract.ShowErrorBlankTitleAndMessage)
    }

    /** Error to show: error while choosing dates for event */
    private suspend fun errorDatesPicked() {
        _editTaskChannel.send(EditTaskEventContract.ShowErrorDatesPicked)
    }

    /** Error to show: error while setting alarm */
    private suspend fun errorAlarmTime() {
        _editTaskChannel.send(EditTaskEventContract.ShowErrorAlarmTime)
    }

    companion object {
        const val ONE_DAY_MLS = 86400000L
        const val ONE_SEC_MLS = 1000
    }
}
