package com.entin.lighttasks.presentation.ui.addedit.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.FragmentEditTaskBinding
import com.entin.lighttasks.presentation.ui.addedit.contract.EditTaskEvent
import com.entin.lighttasks.presentation.ui.addedit.viewmodel.AddEditTaskViewModel
import com.entin.lighttasks.presentation.util.getSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Fragment for adding new task or editing existing task
 */

@AndroidEntryPoint
class EditTaskFragment : Fragment(R.layout.fragment_edit_task) {

    private val binding by viewBinding(FragmentEditTaskBinding::bind)

    private val vm: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEventObserver()

        setupFields()
    }

    private fun setupEventObserver() = vm.editTaskChannel
        .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
        .onEach { event ->
            when (event) {
                /**
                 * Navigation back
                 */
                is EditTaskEvent.NavBackWithResult -> {
                    eventNavBackWithResult(event)
                }
                /**
                 * Error show
                 */
                is EditTaskEvent.ShowErrorBlankTitleText -> {
                    getSnackBar(
                        resources.getString(R.string.snack_bar_empty_task_title_forbidden),
                        requireView()
                    ).show()
                }
            }
        }.launchIn(lifecycleScope)

    private fun setupFields() = with(binding) {
        addNewTaskTitle.setText(vm.taskTitle)
        addNewTaskMessage.setText(vm.taskMessage)
        addNewTaskFinishedCheckbox.isChecked = vm.taskFinished
        addNewTaskFinishedCheckbox.jumpDrawablesToCurrentState()
        addNewTaskImportantCheckbox.isChecked = vm.taskImportant
        addNewTaskImportantCheckbox.jumpDrawablesToCurrentState()
        addNewTaskRadiogroupIcons.check(vm.taskGroup)

        // Two fields and radio group

        addNewTaskRadiogroupIcons.setOnCheckedChangeListener { _, checkedId ->
            vm.taskGroup = checkedId
        }

        addNewTaskTitle.addTextChangedListener {
            vm.taskTitle = it.toString()
        }

        addNewTaskMessage.addTextChangedListener {
            vm.taskMessage = it.toString()
        }

        // Two check buttons [Important and Finished]

        addNewTaskFinishedCheckbox.setOnCheckedChangeListener { _, isCheck ->
            vm.taskFinished = isCheck
        }

        addNewTaskImportantCheckbox.setOnCheckedChangeListener { _, isCheck ->
            vm.taskImportant = isCheck
        }

        // OK Button

        addNewTaskOkButton.setOnClickListener {
            vm.saveTaskBtnClicked()
        }
    }

    // Event: navigate to AllTasksFragment with result
    private fun eventNavBackWithResult(event: EditTaskEvent.NavBackWithResult) {
        binding.addNewTaskTitle.clearFocus()
        setFragmentResult(
            "operationMode",
            bundleOf("mode" to event.typeNewOrEdit)
        )
        findNavController().popBackStack()
    }
}