package com.example.tasksexample.presentation.fragment.addedit.fragment

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
import com.example.tasksexample.R
import com.example.tasksexample.databinding.FragmentEditTaskBinding
import com.example.tasksexample.presentation.fragment.addedit.contract.EditTaskEvent
import com.example.tasksexample.presentation.fragment.addedit.viewmodel.EditTaskViewModel
import com.example.tasksexample.presentation.util.getSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class EditTaskFragment : Fragment(R.layout.fragment_edit_task) {

    private val binding: FragmentEditTaskBinding by viewBinding()

    private val vm: EditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        setupFields()
    }

    private fun setupObservers() = vm.editTaskChannel
        .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
        .onEach { event ->
            when (event) {
                is EditTaskEvent.NavBackWithResult -> {
                    binding.addNewTaskTitle.clearFocus()
                    setFragmentResult(
                        "operationMode",
                        bundleOf("mode" to event.typeNewOrEdit)
                    )
                    findNavController().popBackStack()
                }

                is EditTaskEvent.ShowErrorBlankTitleText -> {
                    getSnackBar(resources.getString(R.string.empty_task_title_forbidden), requireView()).show()
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

        // Click on Image to select radio button

        icGroupNothing.setOnClickListener {
            addNewTaskRadiogroupIcons.check(R.id.radio_nothing)
        }

        icGroupWork.setOnClickListener {
            addNewTaskRadiogroupIcons.check(R.id.radio_work)
        }

        icGroupRest.setOnClickListener {
            addNewTaskRadiogroupIcons.check(R.id.radio_rest)
        }

        icGroupHome.setOnClickListener {
            addNewTaskRadiogroupIcons.check(R.id.radio_home)
        }

        icGroupFood.setOnClickListener {
            addNewTaskRadiogroupIcons.check(R.id.radio_food)
        }

    }
}