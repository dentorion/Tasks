package com.example.tasksexample.presentation.ui.addedit.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasksexample.R
import com.example.tasksexample.domain.entity.Task
import com.example.tasksexample.domain.repository.TasksRepository
import com.example.tasksexample.presentation.ui.addedit.contract.EditTaskEvent
import com.example.tasksexample.presentation.util.AppConstants.TASK_EDIT
import com.example.tasksexample.presentation.util.AppConstants.TASK_NEW
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: TasksRepository
) : ViewModel() {

    val task = state.get<Task>("task")

    var taskTitle = state.get<String>("taskTitle") ?: task?.title ?: ""
        set(value) {
            field = value
            state.set("taskTitle", value)
        }

    var taskMessage = state.get<String>("taskMessage") ?: task?.message ?: ""
        set(value) {
            field = value
            state.set("taskMessage", value)
        }

    var taskFinished = state.get<Boolean>("taskFinished") ?: task?.finished ?: false
        set(value) {
            field = value
            state.set("taskFinished", value)
        }

    var taskImportant = state.get<Boolean>("taskImportant") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportant", value)
        }

    var taskGroup: Int = state.get<Int>("taskGroup") ?: task?.group ?: R.id.radio_nothing
        set(value) {
            field = value
            state.set("taskGroup", value)
        }

    private val _editTaskChannel = Channel<EditTaskEvent>()
    val editTaskChannel = _editTaskChannel.receiveAsFlow()

    fun saveTaskBtnClicked() {
        if (taskTitle.isBlank()) {
            errorBlankText()
            return
        }
        if (task != null) {
            val updatedTask = task.copy(
                title = taskTitle,
                message = taskMessage,
                finished = taskFinished,
                important = taskImportant,
                group = taskGroup
            )
            updateTask(updatedTask)
        } else {
            val newTask = Task(
                title = taskTitle,
                message = taskMessage,
                finished = taskFinished,
                important = taskImportant,
                group = taskGroup
            )
            saveNewTask(newTask)
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