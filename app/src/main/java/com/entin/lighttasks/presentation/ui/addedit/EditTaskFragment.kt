package com.entin.lighttasks.presentation.ui.addedit

import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
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
import com.entin.lighttasks.presentation.ui.addedit.AddEditTaskViewModel.Companion.ONE_DAY_MLS
import com.entin.lighttasks.presentation.ui.addedit.adapter.RadioButtonAdapter
import com.entin.lighttasks.presentation.ui.addedit.adapter.SlowlyLinearLayoutManager
import com.entin.lighttasks.presentation.util.NEW_LINE
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.ZERO_LONG
import com.entin.lighttasks.presentation.util.getSnackBar
import com.entin.lighttasks.presentation.util.toFormattedDateString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date


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
     * Setup fields value
     */
    private fun setupFields() = with(binding) {
        /** Title */
        addEditTaskTitle.setText(viewModel.taskTitle)
        addEditTaskTitle.addTextChangedListener {
            viewModel.taskTitle = it.toString()
        }
        /** Message */
        addEditTaskMessage.setText(viewModel.taskMessage)
        addEditTaskMessage.addTextChangedListener {
            viewModel.taskMessage = it.toString()
        }
        /** Finished */
        addEditTaskFinishedCheckbox.apply {
            this.isChecked = viewModel.taskFinished
            this.jumpDrawablesToCurrentState()
        }
        addEditTaskFinishedCheckbox.setOnCheckedChangeListener { _, isCheck ->
            viewModel.taskFinished = isCheck
        }
        /** Important */
        addEditTaskImportantCheckbox.apply {
            this.isChecked = viewModel.taskImportant
            this.jumpDrawablesToCurrentState()
        }
        addEditTaskImportantCheckbox.setOnCheckedChangeListener { _, isCheck ->
            viewModel.taskImportant = isCheck
        }
        /** Expired */
        addEditTaskExpiredCheckbox.apply {
            this.isChecked = viewModel.isTaskExpired
            this.jumpDrawablesToCurrentState()
            isDatePickersShown()
        }
        addEditTaskExpiredCheckbox.setOnCheckedChangeListener { _, isCheck ->
            viewModel.isTaskExpired = isCheck
            isDatePickersShown()
        }
        /** Share data */
        addEditTaskCircleShare.setOnClickListener {
            val dataToSend = addEditTaskTitle.text.toString() + NEW_LINE + addEditTaskMessage.text.toString()

            try {
                startActivity(
                    Intent.createChooser(
                        Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, dataToSend)
                            type = "text/plain"
                        }, requireContext().getString(R.string.detail_share_to)
                    )
                )
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireContext(), "Error sending.", Toast.LENGTH_SHORT).show()
            }
        }
        /** Radio Group */
        // Event flag
        addEditTaskDatePickerCalendarEvent.isChecked = viewModel.isEvent && !viewModel.isRange
        // Range flag
        addEditTaskDatePickerCalendarRange.isChecked = viewModel.isRange && !viewModel.isEvent
        // is Event (not range) listener
        addEditTaskDatePickerRadioGroup.setOnCheckedChangeListener { _, checkId ->
            when (checkId) {
                binding.addEditTaskDatePickerCalendarEvent.id -> {
                    viewModel.isEvent = true
                    viewModel.isRange = false
                }

                binding.addEditTaskDatePickerCalendarRange.id -> {
                    viewModel.isEvent = false
                    viewModel.isRange = true
                }

                else -> {
                    Log.e("GlobalErrors", "Error setting type: event / range.")
                }
            }
            addEditTaskDatePickerSecond.isVisible = viewModel.isRange && !viewModel.isEvent
        }
        /** First Date picker */
        addEditTaskDatePickerFirst.setOnClickListener {
            val date = Calendar.getInstance().apply {
                time = Date(replaceZeroDate(viewModel.taskExpireFirstDate))
            }
            DatePickerDialog(
                requireContext(), R.style.DatePickerStyle, null,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
            ).also { dialog ->
                dialog.apply {
                    datePicker.minDate = Date().time
                    setOnDateSetListener { _, year, month, day ->
                        val selectedDateCalendar = Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, day)
                            set(Calendar.HOUR_OF_DAY, ZERO)
                            set(Calendar.MINUTE, ZERO)
                            set(Calendar.SECOND, ZERO)
                        }
                        viewModel.taskExpireFirstDate = selectedDateCalendar.timeInMillis
                        setFirstDateOfExpire()
                    }
                    show()
                }
            }
        }
        setFirstDateOfExpire()
        /** Second Date picker */
        addEditTaskDatePickerSecond.isVisible =
            viewModel.isTaskExpired && !viewModel.isEvent && viewModel.isRange
        addEditTaskDatePickerSecond.setOnClickListener {
            val date = Calendar.getInstance().apply {
                time = Date(replaceZeroDate(viewModel.taskExpireSecondDate))
            }
            DatePickerDialog(
                requireContext(),
                R.style.DatePickerStyle,
                null,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH),
            ).also { dialog ->
                dialog.apply {
                    datePicker.minDate =
                        replaceZeroDate(viewModel.taskExpireFirstDate) + ONE_DAY_MLS
                    setOnDateSetListener { _, year, month, day ->
                        val selectedDateCalendar = Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, day)
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                        }
                        viewModel.taskExpireSecondDate = selectedDateCalendar.timeInMillis
                        setSecondDateOfExpire()
                    }
                    show()
                }
            }
        }
        setSecondDateOfExpire()
        /** OK Button */
        addEditTaskOkButton.setOnClickListener {
            viewModel.saveTaskBtnClicked()
        }
    }

    private fun setFirstDateOfExpire() {
        binding.addEditTaskDatePickerFirst.text = if (viewModel.taskExpireFirstDate == ZERO_LONG) {
            Date().time.toFormattedDateString()
        } else {
            viewModel.taskExpireFirstDate.toFormattedDateString()
        }
    }

    private fun setSecondDateOfExpire() {
        binding.addEditTaskDatePickerSecond.text =
            if (viewModel.taskExpireSecondDate == ZERO_LONG) {
                (Date().time + ONE_DAY_MLS).toFormattedDateString()
            } else {
                viewModel.taskExpireSecondDate.toFormattedDateString()
            }
    }

    private fun onGroupIconSelected(element: TaskGroup) {
        viewModel.taskGroup = element.groupId
    }

    private fun isDatePickersShown() {
        binding.apply {
            // Label
            addEditTaskAddDateLabel.isVisible = !viewModel.isTaskExpired
            addEditTaskAddDateLabelArrow.isVisible = !viewModel.isTaskExpired
            // Radio flags of event / radio
            addEditTaskDatePickerCalendarEvent.isVisible = viewModel.isTaskExpired
            addEditTaskDatePickerCalendarRange.isVisible = viewModel.isTaskExpired
            // Date pickers with date as title
            addEditTaskDatePickerFirst.isVisible =
                viewModel.isTaskExpired && (viewModel.isRange || viewModel.isEvent)
            addEditTaskDatePickerSecond.isVisible = viewModel.isTaskExpired && viewModel.isRange
        }
    }

    private fun replaceZeroDate(date: Long): Long = if (date == ZERO_LONG) Date().time else date

    // Event: navigate to AllTasksFragment with result
    private fun eventNavBackWithResult(event: Int) {
        binding.addEditTaskTitle.clearFocus()
        setFragmentResult(
            "operationMode",
            bundleOf("mode" to event),
        )
        findNavController().popBackStack()
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
                     * Error blank title text show
                     */
                    is EditTaskEventContract.ShowErrorBlankTitleText -> {
                        getSnackBar(
                            resources.getString(R.string.snack_bar_empty_task_title_forbidden),
                            requireView(),
                        ).show()
                    }
                    /**
                     * Error dates picked show
                     */
                    is EditTaskEventContract.ShowErrorDatesPicked -> {
                        getSnackBar(
                            resources.getString(R.string.snack_bar_dates_picked),
                            requireView(),
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        groupAdapter = null
        _binding = null
        super.onDestroyView()
    }
}
