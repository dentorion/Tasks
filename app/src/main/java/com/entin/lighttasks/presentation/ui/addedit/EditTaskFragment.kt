package com.entin.lighttasks.presentation.ui.addedit

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.entin.lighttasks.presentation.util.NEW_LINE
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

        binding.addEditTaskCategoryRecyclerview.apply {
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
        addEditTaskTitle.setText(viewModel.taskTitle)
        addEditTaskMessage.setText(viewModel.taskMessage)
        addEditTaskFinishedCheckbox.isChecked = viewModel.taskFinished
        addEditTaskFinishedCheckbox.jumpDrawablesToCurrentState()
        addEditTaskImportantCheckbox.isChecked = viewModel.taskImportant
        addEditTaskImportantCheckbox.jumpDrawablesToCurrentState()

        // Share data

        addEditTaskCircleShare.setOnClickListener {
            val dataToSend = addEditTaskTitle.text.toString() + NEW_LINE + addEditTaskMessage.text.toString()

            try {
                startActivity(
                    Intent.createChooser(
                        Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, dataToSend)
                            type = "text/plain"
                        },
                        requireContext().getString(R.string.detail_share_to)
                    )
                )
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireContext(), "Error sending.", Toast.LENGTH_SHORT).show()
            }
        }

        // Two fields

        addEditTaskTitle.addTextChangedListener {
            viewModel.taskTitle = it.toString()
        }

        addEditTaskMessage.addTextChangedListener {
            viewModel.taskMessage = it.toString()
        }

        // Two check buttons [Important and Finished]

        addEditTaskFinishedCheckbox.setOnCheckedChangeListener { _, isCheck ->
            viewModel.taskFinished = isCheck
        }

        addEditTaskImportantCheckbox.setOnCheckedChangeListener { _, isCheck ->
            viewModel.taskImportant = isCheck
        }

        // OK Button

        addEditTaskOkButton.setOnClickListener {
            viewModel.saveTaskBtnClicked()
        }
    }

    private fun onGroupIconSelected(element: TaskGroup) {
        viewModel.taskGroup = element.groupId
    }

    // Event: navigate to AllTasksFragment with result
    private fun eventNavBackWithResult(event: Int) {
        binding.addEditTaskTitle.clearFocus()
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
