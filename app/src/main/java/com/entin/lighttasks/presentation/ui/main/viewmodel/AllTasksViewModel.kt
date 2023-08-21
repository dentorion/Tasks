package com.entin.lighttasks.presentation.ui.main.viewmodel

import androidx.lifecycle.*
import com.entin.lighttasks.data.util.datastore.Preferences
import com.entin.lighttasks.domain.entity.OrderSort
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.ui.main.contract.AddEditTaskMessage
import com.entin.lighttasks.presentation.ui.main.contract.AllTasksEvent
import com.entin.lighttasks.presentation.util.TASK_EDIT
import com.entin.lighttasks.presentation.util.TASK_NEW
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    private val searchFlow = combine(
        searchValue.asFlow(),
        flowSortingPreferences
    ) { search, prefs ->
        Pair(search, prefs)
    }.flatMapLatest { request ->
        repository.getAllTasksWithSorting(
            query = request.first,
            orderSort = request.second.sortByTitleDateImportantManual,
            hideFinished = request.second.hideFinished
        )
    }

    val tasks = searchFlow.asLiveData()

    init {
        viewModelScope.launch {
            flowSortingPreferences.collect {
                isManualSorting = it.sortByTitleDateImportantManual == OrderSort.SORT_BY_MANUAL
            }
        }
    }

    // SORTING Tasks

    fun updateFinishedOrder(hideFinished: Boolean) = viewModelScope.launch {
        preferences.updateFinishedSort(hideFinished)
    }

    fun updateSortingOrder(argument: OrderSort) = viewModelScope.launch {
        preferences.updateSortOrder(argument)
    }

    // TASK

    fun onTaskClick(task: Task) = viewModelScope.launch {
        _tasksEvent.send(AllTasksEvent.NavToEditTask(task))
    }

    fun onFinishedTaskClick(task: Task, isChecked: Boolean) = viewModelScope.launch {
        repository.updateTask(task.copy(finished = isChecked))
    }

    fun onTaskSwipedDelete(task: Task) = diAppScope.launch {
        repository.deleteTask(task)
        _tasksEvent.send(AllTasksEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        repository.newTask(task).collect { result ->
            if (result) _tasksEvent.send(AllTasksEvent.Smile)
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

    // Update list after manual changing position of Task
    fun updateAllTasks(list: List<Task>) = viewModelScope.launch {
        repository.updateAllTasks(list)
    }
}