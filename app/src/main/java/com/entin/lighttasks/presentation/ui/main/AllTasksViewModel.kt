package com.entin.lighttasks.presentation.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.data.util.datastore.Preferences
import com.entin.lighttasks.domain.entity.OrderSort
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.TASK_EDIT
import com.entin.lighttasks.presentation.util.TASK_NEW
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@HiltViewModel
class AllTasksViewModel @Inject constructor(
    state: SavedStateHandle,
    private val repository: TasksRepository,
    private val preferences: Preferences,
    @Named("AppScopeDI") private val diAppScope: CoroutineScope,
) : ViewModel() {

    val searchValue = state.getLiveData("searchValue", "")
    val flowSortingPreferences = preferences.preferencesFlow

    private val _tasksEvent = Channel<AllTasksEvent>()
    val tasksEvent = _tasksEvent.receiveAsFlow()

    var isManualSorting: Boolean = false
        private set

    private var hideDatePickedTasks: Boolean = true
    private var isASCSorting: Boolean = true
    private var sortType: OrderSort = OrderSort.SORT_BY_DATE

    private val searchFlow = combine(
        searchValue.asFlow(),
        flowSortingPreferences,
    ) { search, prefs ->
        Pair(search, prefs)
    }.flatMapLatest { request ->
        repository.getAllTasksWithSorting(
            query = request.first,
            orderSort = request.second.sortByTitleDateImportantManual,
            hideFinished = request.second.hideFinished,
            isAsc = request.second.sortASC,
            hideDatePick = request.second.hideDatePickedTasks,
        )
    }

    val tasks: LiveData<List<Task>> = searchFlow.asLiveData()

    init {
        viewModelScope.launch {
            flowSortingPreferences.collect {
                isManualSorting = it.sortByTitleDateImportantManual == OrderSort.SORT_BY_MANUAL
                sortType = it.sortByTitleDateImportantManual
                isASCSorting = it.sortASC
                hideDatePickedTasks = it.hideDatePickedTasks
            }
        }
    }

    // SORTING Tasks

    fun updateShowFinishedTask(hideFinished: Boolean) {
        viewModelScope.launch {
            preferences.updateFinishedSort(hideFinished)
        }
    }

    fun updateShowDatePickedTask(hideDatePicked: Boolean) {
        viewModelScope.launch {
            preferences.updateShowDatePickedTask(hideDatePicked)
        }
    }

    fun updateSortASC() {
        viewModelScope.launch {
            preferences.updateSortASC(isASCSorting.not())
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
            repository.updateTask(task.copy(finished = isChecked))
        }
    }

    fun onTaskSwipedDelete(task: Task) {
        diAppScope.launch {
            repository.deleteTask(task)
            _tasksEvent.send(AllTasksEvent.ShowUndoDeleteTaskMessage(task))
        }
    }


    fun onUndoDeleteClick(task: Task) {
        diAppScope.launch {
            repository.newTask(task).collect { result ->
                if (result) _tasksEvent.send(AllTasksEvent.Smile)
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

    fun deleteFinishedTasks(callBackDismiss: () -> Unit) = viewModelScope.launch {
        repository.deleteFinishedTasks()
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
        repository.updateAllTasks(list)
    }

    fun onTaskSortByIcon(task: Task) {
        updateSortingOrder(OrderSort.SORT_BY_ICON.apply { groupId = task.group })
    }
}
