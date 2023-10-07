package com.entin.lighttasks.presentation.screens.addedit

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
import com.entin.lighttasks.domain.entity.IconTask
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.presentation.screens.addedit.AddEditTaskViewModel.Companion.ONE_DAY_MLS
import com.entin.lighttasks.presentation.screens.addedit.adapter.IconTaskAdapter
import com.entin.lighttasks.presentation.screens.addedit.adapter.SlowlyLinearLayoutManager
import com.entin.lighttasks.presentation.screens.dialogs.LinkAddToTaskDialog
import com.entin.lighttasks.presentation.screens.dialogs.PhotoAddToTaskDialog
import com.entin.lighttasks.presentation.screens.dialogs.PhotoShowDialog
import com.entin.lighttasks.presentation.screens.dialogs.SectionChooseDialog
import com.entin.lighttasks.presentation.screens.dialogs.VoiceAddToTaskDialog
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.NEW_LINE
import com.entin.lighttasks.presentation.util.ZERO_LONG
import com.entin.lighttasks.presentation.util.checkForEmptyTitle
import com.entin.lighttasks.presentation.util.getCurrentDay
import com.entin.lighttasks.presentation.util.getCurrentMonth
import com.entin.lighttasks.presentation.util.getCurrentYear
import com.entin.lighttasks.presentation.util.getFinishDate
import com.entin.lighttasks.presentation.util.getSnackBar
import com.entin.lighttasks.presentation.util.getStartDate
import com.entin.lighttasks.presentation.util.getTimeMls
import com.entin.lighttasks.presentation.util.replaceZeroDateWithNow
import com.entin.lighttasks.presentation.util.toFormattedDateString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Fragment for adding new task or editing existing task
 */

@AndroidEntryPoint
class EditTaskFragment : Fragment(R.layout.fragment_edit_task) {
    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditTaskViewModel by viewModels()
    private var groupAdapter: IconTaskAdapter? = null

    // Link add dialog
    @OptIn(ExperimentalCoroutinesApi::class)
    private val linkAddEditDialog by lazy {
        LinkAddToTaskDialog()
    }

    // Photo add dialog
    @OptIn(ExperimentalCoroutinesApi::class)
    private val photoAddEditDialog by lazy {
        PhotoAddToTaskDialog()
    }

    // Photo show dialog
    @OptIn(ExperimentalCoroutinesApi::class)
    private val photoShowDialog by lazy {
        PhotoShowDialog()
    }

    // Voice add Dialog
    @OptIn(ExperimentalCoroutinesApi::class)
    private val voiceAddEditDialog by lazy {
        VoiceAddToTaskDialog()
    }

    // Section choose dialog
    @OptIn(ExperimentalCoroutinesApi::class)
    private val sectionChooseDialog by lazy {
        SectionChooseDialog(::onSectionSelect)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditTaskBinding.inflate(inflater, container, false)

        viewModel.getIcons()
        viewModel.getSectionById()
        setupCategoryRecyclerView()
        setupSectionNameObserver()
        setupEventObserver()
        setupFields()

        return binding.root
    }

    /**
     * Category
     */
    private fun setupCategoryRecyclerView() {
        groupAdapter = IconTaskAdapter(viewModel.taskGroup) { element, position ->
            onGroupIconSelected(element, position)
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
                viewModel.iconTaskChannel.collect { icons ->
                    onIconsGet(icons)
                }
            }
        }
    }

    private fun setupSectionNameObserver() {
        viewModel.sectionName.observe(viewLifecycleOwner) {
            binding.addEditTaskSectionSelection.text = if(it == EMPTY_STRING) {
                resources.getString(R.string.no_section)
            } else {
                it
            }
        }
    }

    /**
     * On list of icons get
     */
    private fun onIconsGet(icons: List<IconTask>) {
        val selectedIcon = icons.first { it.groupId == viewModel.taskGroup }
        val indexOfSelectedIcon = icons.indexOf(selectedIcon)
        groupAdapter?.submitList(icons)
        val marginElements = if (indexOfSelectedIcon >= 2) 2 else 0
        binding.addEditTaskCategoryRecyclerview.scrollToPosition(indexOfSelectedIcon - marginElements)
    }

    /**
     * Setup fields value
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setupFields() {
        with(binding) {
            /** Title */
            viewModel.task?.let {
                addEditTaskTitle.setText(
                    checkForEmptyTitle(
                        viewModel.taskTitle, resources, viewModel.getTaskId()
                    )
                )
            } ?: kotlin.run {
                EMPTY_STRING
            }
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
                val dataToSend =
                    addEditTaskTitle.text.toString() + NEW_LINE + addEditTaskMessage.text.toString()

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
                addEditTaskDatePickerSecond.isVisible =
                    viewModel.isTaskExpired && !viewModel.isEvent && viewModel.isRange
            }
            /** First Date picker */
            addEditTaskDatePickerFirst.setOnClickListener {
                val date = Calendar.getInstance().apply {
                    timeInMillis = replaceZeroDateWithNow(viewModel.taskExpireFirstDate)
                }
                DatePickerDialog(
                    requireContext(),
                    R.style.DatePickerStyle,
                    null,
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH)
                ).also { dialog ->
                    dialog.apply {
                        datePicker.minDate = getTimeMls()
                        setOnDateSetListener { _, year, month, day ->
                            viewModel.taskExpireFirstDate = getStartDate(year, month, day)
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
                    timeInMillis = replaceZeroDateWithNow(viewModel.taskExpireSecondDate)
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
                            replaceZeroDateWithNow(viewModel.taskExpireFirstDate) + ONE_DAY_MLS
                        setOnDateSetListener { _, year, month, day ->
                            viewModel.taskExpireSecondDate = getFinishDate(year, month, day)
                            setSecondDateOfExpire()
                        }
                        show()
                    }
                }
            }
            setSecondDateOfExpire()
            /** Photo Attached */
            addEditTaskPhoto.setOnClickListener {
                if (!photoAddEditDialog.isVisible) {
                    photoAddEditDialog.show(childFragmentManager, PhotoAddToTaskDialog::class.simpleName)
                }
            }
            /** Link Attached */
            addEditTaskLink.setOnClickListener {
                if (!linkAddEditDialog.isVisible) {
                    linkAddEditDialog.show(childFragmentManager, LinkAddToTaskDialog::class.simpleName)
                }
            }
            /** Voice Attached */
            addEditTaskVoice.setOnClickListener {
                if (!voiceAddEditDialog.isVisible) {
                    voiceAddEditDialog.show(childFragmentManager, VoiceAddToTaskDialog::class.simpleName)
                }
            }
            /** Tag url */
            addEditTaskUrlTag.apply{
                setTagUrlVisibility(viewModel.linkAttached.isNotEmpty())
                setOnClickListener {
                    findNavController().navigate(
                        EditTaskFragmentDirections.actionGlobalUrlWebView(viewModel.linkAttached)
                    )
                }
            }
            /** Tag photo */
            addEditTaskPhotoTag.apply{
                isVisible = viewModel.photoAttached.isNotEmpty()
                setOnClickListener {
                    if (!photoShowDialog.isVisible) {
                        photoShowDialog.show(childFragmentManager, PhotoShowDialog::class.simpleName)
                    }
                }
            }
            /** Tag voice */
            addEditTaskVoiceTag.apply{
                isVisible = viewModel.voiceAttached.isNotEmpty()
                setOnClickListener {
                    if (!voiceAddEditDialog.isVisible) {
                        voiceAddEditDialog.show(childFragmentManager, VoiceAddToTaskDialog::class.simpleName)
                    }
                }
            }
            /** Choose section (category of task) */
            addEditTaskSectionSelection.setOnClickListener {
                if (!sectionChooseDialog.isVisible) {
                    sectionChooseDialog.show(childFragmentManager, SectionChooseDialog::class.simpleName)
                }
            }
            /** OK Button */
            addEditTaskOkButton.setOnClickListener {
                viewModel.saveTaskBtnClicked()
            }
        }
    }

    private fun setFirstDateOfExpire() {
        binding.addEditTaskDatePickerFirst.text = if (viewModel.taskExpireFirstDate == ZERO_LONG) {
            getStartDate(year = getCurrentYear(), month = getCurrentMonth(), day = getCurrentDay()).toFormattedDateString()
        } else {
            viewModel.taskExpireFirstDate.toFormattedDateString()
        }
    }

    private fun setSecondDateOfExpire() {
        binding.addEditTaskDatePickerSecond.text =
            if (viewModel.taskExpireSecondDate == ZERO_LONG) {
                (getFinishDate(
                    year = getCurrentYear(),
                    month = getCurrentMonth(),
                    day = getCurrentDay()) + ONE_DAY_MLS
                ).toFormattedDateString()
            }
            else if (viewModel.taskExpireFirstDate == viewModel.taskExpireSecondDate) {
                /**
                 * User choose event type without changing dates, save.
                 * After change type to range and if start was today, finish will be tomorrow by auto,
                 * but if start was today + N days -> user have to reselect finish day manually.
                 * That's why after reselecting from event to range we can pass finish as tomorrow.
                 * But the best way to set finish date: start date + end of next day.
                 */
                viewModel.taskExpireSecondDate = viewModel.defaultFinishDateTime
                viewModel.taskExpireSecondDate.toFormattedDateString()
            }
            else {
                viewModel.taskExpireSecondDate.toFormattedDateString()
            }
    }

    private fun onGroupIconSelected(element: IconTask, position: Int?) {
        position?.let {
            viewModel.taskGroup = element.groupId
            binding.addEditTaskCategoryRecyclerview.smoothScrollToPosition(it)
        }
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
            addEditTaskDatePickerFirst.isVisible = viewModel.isTaskExpired && (viewModel.isRange || viewModel.isEvent)
            addEditTaskDatePickerSecond.isVisible = viewModel.isTaskExpired && viewModel.isRange
        }
    }

    /**
     * Event: navigate to AllTasksFragment with result
     */
    private fun eventNavBackWithResult(event: Int) {
        binding.addEditTaskTitle.clearFocus()
        setFragmentResult("operationMode", bundleOf("mode" to event))
        findNavController().popBackStack()
    }

    /**
     * Event observer
     */
    private fun setupEventObserver() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.editTaskChannel.collect { event: EditTaskEventContract ->
                when (event) {
                    /** Navigation back */
                    is EditTaskEventContract.NavBackWithResult -> {
                        eventNavBackWithResult(event.typeNewOrEditorExist)
                    }
                    /** Error blank title text show */
                    is EditTaskEventContract.ShowErrorBlankTitleAndMessage -> {
                        getSnackBar(
                            resources.getString(R.string.snack_bar_empty_task_title_forbidden),
                            requireView(),
                        ).show()
                    }
                    /** Error dates picked show */
                    is EditTaskEventContract.ShowErrorDatesPicked -> {
                        getSnackBar(
                            resources.getString(R.string.snack_bar_dates_picked),
                            requireView(),
                        ).show()
                    }
                    /** Task is not saved */
                    is EditTaskEventContract.TaskNotSaved -> {
                        getSnackBar(
                            resources.getString(R.string.snack_bar_task_not_saved),
                            requireView(),
                        ).show()
                    }

                    is EditTaskEventContract.RefreshTagsVisibility -> {
                        setTagUrlVisibility(event.url)
                        setTagPhotoVisibility(event.photo)
                    }
                }
            }
        }
    }

    private fun onSectionSelect(section: Section) {
        viewModel.sectionId = section.id
        viewModel.getSectionById()
    }

    private fun setTagUrlVisibility(value: Boolean) {
        binding.addEditTaskUrlTag.isVisible = value
    }

    private fun setTagPhotoVisibility(value: Boolean) {
        binding.addEditTaskPhotoTag.isVisible = value
    }

    override fun onDestroyView() {
        groupAdapter = null
        _binding = null
        super.onDestroyView()
    }
}
