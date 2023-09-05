package com.entin.lighttasks.presentation.ui.addedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.FragmentEditTaskBinding
import com.entin.lighttasks.domain.entity.TaskGroup
import com.entin.lighttasks.presentation.ui.addedit.adapter.RadioButtonAdapter
import com.entin.lighttasks.presentation.ui.addedit.adapter.SlowlyLinearLayoutManager
import com.entin.lighttasks.presentation.util.getSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment for adding new task or editing existing task
 */

@AndroidEntryPoint
class EditTaskFragment : Fragment(R.layout.fragment_edit_task) {
    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditTaskViewModel by viewModels()
    private var groupAdapter: RadioButtonAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditTaskBinding.inflate(inflater, container, false)

        setupCategoryRecyclerView()
        setupEventObserver()
        setupFields()

        return binding.root
    }

    /** Category */
    private fun setupCategoryRecyclerView() {
        groupAdapter = RadioButtonAdapter(viewModel.taskGroup) { element ->
            onGroupIconSelected(element)
        }

        binding.categoryRecyclerView.apply {
            adapter = groupAdapter
            layoutManager = SlowlyLinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false,
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.taskGroupsChannel.collect { radioButtonElements ->
                    groupAdapter?.submitList(radioButtonElements)
                }
            }
        }
    }

    /**
     * Event observer
     */
    private fun setupEventObserver() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.editTaskChannel.collect { event: EditTaskEventContract ->
                when (event) {
                    /**
                     * Navigation back
                     */
                    is EditTaskEventContract.NavBackWithResult -> {
                        eventNavBackWithResult(event.typeNewOrEditorExist)
                    }
                    /**
                     * Error show
                     */
                    is EditTaskEventContract.ShowErrorBlankTitleText -> {
                        getSnackBar(
                            resources.getString(R.string.snack_bar_empty_task_title_forbidden),
                            requireView(),
                        ).show()
                    }
                }
            }
        }
    }

    /**
     * Setup fields value
     */
    private fun setupFields() = with(binding) {
        addNewTaskTitle.setText(viewModel.taskTitle)
        addNewTaskMessage.setText(viewModel.taskMessage)
        addNewTaskFinishedCheckbox.isChecked = viewModel.taskFinished
        addNewTaskFinishedCheckbox.jumpDrawablesToCurrentState()
        addNewTaskImportantCheckbox.isChecked = viewModel.taskImportant
        addNewTaskImportantCheckbox.jumpDrawablesToCurrentState()

        // Radio group

        // make()

        // Two fields

        addNewTaskTitle.addTextChangedListener {
            viewModel.taskTitle = it.toString()
        }

        addNewTaskMessage.addTextChangedListener {
            viewModel.taskMessage = it.toString()
        }

        // Two check buttons [Important and Finished]

        addNewTaskFinishedCheckbox.setOnCheckedChangeListener { _, isCheck ->
            viewModel.taskFinished = isCheck
        }

        addNewTaskImportantCheckbox.setOnCheckedChangeListener { _, isCheck ->
            viewModel.taskImportant = isCheck
        }

        // OK Button

        addNewTaskOkButton.setOnClickListener {
            viewModel.saveTaskBtnClicked()
        }
    }

    private fun onGroupIconSelected(element: TaskGroup) {
        viewModel.taskGroup = element.groupId
    }

    // Event: navigate to AllTasksFragment with result
    private fun eventNavBackWithResult(event: Int) {
        binding.addNewTaskTitle.clearFocus()
        setFragmentResult(
            "operationMode",
            bundleOf("mode" to event),
        )
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        groupAdapter = null
        _binding = null
        super.onDestroyView()
    }
}
