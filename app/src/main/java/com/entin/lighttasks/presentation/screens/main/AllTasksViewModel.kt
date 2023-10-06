package com.entin.lighttasks.presentation.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.data.util.datastore.Preferences
import com.entin.lighttasks.domain.entity.OrderSort
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.domain.entity.SortPreferences
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.repository.SectionsRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.TASK_EDIT
import com.entin.lighttasks.presentation.util.TASK_NEW
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@HiltViewModel
class AllTasksViewModel @Inject constructor(
    state: SavedStateHandle,
    private val taskRepository: TasksRepository,
    private val sectionsRepository: SectionsRepository,
    private val preferences: Preferences,
    @Named("AppScopeDI") private val diAppScope: CoroutineScope,
) : ViewModel() {

    /** Search value */
    val searchValue = state.getLiveData(SEARCH_VALUE, EMPTY_STRING)

    /** Preferences Flow to get tasks from DB */
    val flowSortingPreferences = preferences.preferencesFlow

    /** For checking - show details or move on long press task */
    var isManualSorting = false
        private set

    init {
        viewModelScope.launch {
            flowSortingPreferences.collect{
                isManualSorting = it.sortByTitleDateImportantManual == OrderSort.SORT_BY_MANUAL
            }
        }
    }

    /** Events for AllTaskFragment */
    private val _tasksEvent = Channel<AllTasksEvent>()
    val tasksEvent = _tasksEvent.receiveAsFlow()

    /** Get tasks by preferences */
    private val searchFlow = combine(
        searchValue.asFlow(),
        flowSortingPreferences,
    ) { search, prefs ->
        Pair(search, prefs)
    }.flatMapLatest { request: Pair<String, SortPreferences> ->
        taskRepository.getAllTasksWithSorting(
            query = request.first,
            orderSort = request.second.sortByTitleDateImportantManual,
            hideFinished = request.second.hideFinished,
            isAsc = request.second.sortASC,
            hideDatePick = request.second.hideEvents,
            sectionId = request.second.sectionId,
        )
    }
    var tasks: LiveData<List<Task>> = searchFlow.asLiveData()

    /** Get sections (group of tasks) */
    private val sectionFlow = sectionsRepository.getAllSections()
    var sections: LiveData<List<Section>> = sectionFlow.asLiveData()

    // SORTING Tasks

    fun updateShowFinishedTask(showFinished: Boolean) {
        viewModelScope.launch {
            preferences.updateShowFinished(showFinished)
        }
    }

    fun updateShowEvents(showEvents: Boolean) {
        viewModelScope.launch {
            preferences.updateShowEvents(showEvents)
        }
    }

    fun updateSortASC() {
        viewModelScope.launch {
            preferences.updateSortASC(
                flowSortingPreferences.first().sortASC.not()
            )
        }
    }

    fun updateSortingOrder(argument: OrderSort) {
        viewModelScope.launch {
            preferences.updateSortOrder(argument)
        }
    }

    // TASK

    fun onTaskClick(task: Task) {
        viewModelScope.launch {
            _tasksEvent.send(AllTasksEvent.NavToEditTask(task))
        }
    }

    fun onFinishedTaskClick(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isFinished = isChecked))
        }
    }

    fun onTaskSwipedDelete(task: Task) {
        diAppScope.launch {
            taskRepository.deleteTask(task)
            _tasksEvent.send(AllTasksEvent.ShowUndoDeleteTaskMessage(task))
        }
    }

    fun onUndoDeleteClick(task: Task) {
        diAppScope.launch {
            taskRepository.newTask(task).collect { result ->
                if (result) _tasksEvent.send(AllTasksEvent.RestoreTaskWithoutPhoto)
            }
        }
    }

    // Navigate to

    fun navToDelete() = viewModelScope.launch {
        _tasksEvent.send(AllTasksEvent.NavToDellFinishedTasks)
    }

    fun addNewTask() = viewModelScope.launch {
        _tasksEvent.send(AllTasksEvent.NavToNewTask)
    }

    // Messages
    fun onEditResultShow(result: Int) = viewModelScope.launch {
        when (result) {
            TASK_NEW -> _tasksEvent.send(AllTasksEvent.ShowAddEditTaskMessage(AddEditTaskMessage.NEW))
            TASK_EDIT -> _tasksEvent.send(AllTasksEvent.ShowAddEditTaskMessage(AddEditTaskMessage.EDIT))
        }
    }

    // Delete task with status finished
    fun deleteFinishedTasks(callBackDismiss: () -> Unit) = viewModelScope.launch {
        taskRepository.deleteFinishedTasks()
        callBackDismiss()
        _tasksEvent.send(AllTasksEvent.ShowDellFinishedTasks)
    }

    // Language
    fun navToChangeLanguage() = viewModelScope.launch {
        _tasksEvent.send(AllTasksEvent.NavToChangeLanguage)
    }

    // Preferences
    fun navToChangePreferences() = viewModelScope.launch {
        _tasksEvent.send(AllTasksEvent.NavToChangePreferences)
    }

    // Update list after manual changing position of Task
    fun updateAllTasks(list: List<Task>) = diAppScope.launch {
        taskRepository.updateAllTasks(list)
    }

    fun onTaskSortByIcon(task: Task) {
        updateSortingOrder(OrderSort.SORT_BY_ICON.apply { groupId = task.group })
    }

    // Calendar open
    fun navToCalendar() {
        viewModelScope.launch(Dispatchers.IO) {
            _tasksEvent.send(AllTasksEvent.NavToCalendar)
        }
    }

    // Section preferences open
    fun navToSectionPreferences() {
        viewModelScope.launch(Dispatchers.IO) {
            _tasksEvent.send(AllTasksEvent.NavToSectionPreferences)
        }
    }

    fun onSectionClick(sectionId: Int) {
        viewModelScope.launch {
            preferences.updateSection(sectionId)
        }
    }

    companion object {
        private const val SEARCH_VALUE = "searchValue"

        data class MainFragmentState(
            val isManualSorting: Boolean,
            val isASCSorting: Boolean,
            val sortType: OrderSort,
            val hideDatePickedTasks: Boolean,
            val sectionId: Int
        )
    }
}
