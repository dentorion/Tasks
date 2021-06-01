package com.example.tasksexample.presentation.ui.main.viewmodel

import androidx.lifecycle.*
import com.example.tasksexample.data.util.datastore.Preferences
import com.example.tasksexample.domain.entity.OrderSort
import com.example.tasksexample.domain.entity.Task
import com.example.tasksexample.domain.repository.TasksRepository
import com.example.tasksexample.presentation.ui.main.contract.AddEditTaskMessage
import com.example.tasksexample.presentation.ui.main.contract.AllTasksEvent
import com.example.tasksexample.presentation.util.AppConstants.TASK_EDIT
import com.example.tasksexample.presentation.util.AppConstants.TASK_NEW
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

@HiltViewModel
class AllTasksViewModel @Inject constructor(
    state: SavedStateHandle,
    private val repository: TasksRepository,
    private val preferences: Preferences,
    @Named("AppScopeDI") private val diAppScope: CoroutineScope
) : ViewModel() {

    val searchValue = state.getLiveData("searchValue", "")
    val flowSortingPreferences = preferences.preferencesFlow

    private val _tasksEvent = Channel<AllTasksEvent>()
    val tasksEvent = _tasksEvent.receiveAsFlow()

    @ExperimentalCoroutinesApi
    private val searchFlow = combine(
        searchValue.asFlow(),
        flowSortingPreferences
    ) { search, prefs ->
        Pair(search, prefs)
    }.flatMapLatest { request ->
        repository.getAllTasksWithSorting(
            request.first,
            request.second.showSortTitleDate,
            request.second.showFinished
        )
    }

    @ExperimentalCoroutinesApi
    val tasks = searchFlow.asLiveData()

    // SORTING Tasks

    fun updateFinishedOrder(argument: Boolean) = viewModelScope.launch {
        preferences.updateFinishedSort(argument)
    }

    fun updateImportantOrder(argument: OrderSort) = viewModelScope.launch {
        preferences.updateImportantOrder(argument)
    }

    fun updateSortOrder(argument: OrderSort) = viewModelScope.launch {
        preferences.updateOrderSort(argument)
    }

    // TASK

    fun onTaskClick(task: Task) = viewModelScope.launch {
        _tasksEvent.send(AllTasksEvent.NavToEditTask(task))
    }

    fun onFinishedTaskClick(task: Task, isChecked: Boolean) = viewModelScope.launch {
        repository.updateTask(task.copy(finished = isChecked))
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
        _tasksEvent.send(AllTasksEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        repository.newTask(task)
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

    fun deleteFinishedTasks() = diAppScope.launch {
        repository.deleteFinishedTasks()
        _tasksEvent.send(AllTasksEvent.ShowDellFinishedTasks)
    }
}