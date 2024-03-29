package com.entin.lighttasks.presentation.screens.addedit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.entin.lighttasks.R
import com.entin.lighttasks.data.db.entity.IconTaskEntity
import com.entin.lighttasks.databinding.FragmentEditTaskBinding
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.presentation.screens.addedit.adapter.IconsTaskAdapter
import com.entin.lighttasks.presentation.screens.addedit.adapter.SlowlyLinearLayoutManager
import com.entin.lighttasks.presentation.screens.dialogs.linkAdd.LinkAddToTaskDialog
import com.entin.lighttasks.presentation.screens.dialogs.photoAdd.PhotoAddToTaskDialog
import com.entin.lighttasks.presentation.screens.dialogs.photoShow.PhotoShowDialog
import com.entin.lighttasks.presentation.screens.dialogs.chooseSection.SectionChooseDialog
import com.entin.lighttasks.presentation.screens.dialogs.voiceRecord.VoiceAddToTaskDialog
import com.entin.lighttasks.presentation.screens.dialogs.galleryImages.GalleryImagesDialog
import com.entin.lighttasks.presentation.screens.dialogs.linkUrl.LinkUrlChooseDialog
import com.entin.lighttasks.presentation.screens.dialogs.security.Security
import com.entin.lighttasks.presentation.screens.dialogs.security.SecurityDialog
import com.entin.lighttasks.presentation.screens.dialogs.security.Place
import com.entin.lighttasks.presentation.screens.dialogs.security.SecurityPurpose
import com.entin.lighttasks.presentation.util.COMMA
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.NEW_LINE
import com.entin.lighttasks.presentation.util.ONE
import com.entin.lighttasks.presentation.util.ONE_DAY_MLS
import com.entin.lighttasks.presentation.util.SUCCESS_ADD_PASSWORD_RESULT
import com.entin.lighttasks.presentation.util.TWO
import com.entin.lighttasks.presentation.util.BUNDLE_IS_PASSWORD_CREATION
import com.entin.lighttasks.presentation.util.BUNDLE_PASSWORD_RESULT_SECURITY_TYPE
import com.entin.lighttasks.presentation.util.BUNDLE_PASSWORD_VALUE
import com.entin.lighttasks.presentation.util.BUNDLE_SECTION_CHOOSE
import com.entin.lighttasks.presentation.util.EDIT_ADD_TASK_TO_ALL_TASKS_EVENT
import com.entin.lighttasks.presentation.util.SUCCESS_CHOOSE_SECTION_RESULT
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.ZERO_LONG
import com.entin.lighttasks.presentation.util.checkForEmptyTitle
import com.entin.lighttasks.presentation.util.getCurrentDay
import com.entin.lighttasks.presentation.util.getCurrentMonth
import com.entin.lighttasks.presentation.util.getCurrentYear
import com.entin.lighttasks.presentation.util.getDefaultStartDateTime
import com.entin.lighttasks.presentation.util.getFinishDate
import com.entin.lighttasks.presentation.util.getSnackBar
import com.entin.lighttasks.presentation.util.getStartDate
import com.entin.lighttasks.presentation.util.getTimeMls
import com.entin.lighttasks.presentation.util.replaceZeroDateWithNow
import com.entin.lighttasks.presentation.util.toFormattedDateString
import com.entin.lighttasks.presentation.util.toFormattedDateTimeString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

/**
 * Fragment for adding new task or editing existing task
 */

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_edit_task) {
    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditTaskViewModel by viewModels()
    private var iconsTaskAdapter: IconsTaskAdapter? = null

    /** Permissions for notification */
    private var activityResultLauncher: ActivityResultLauncher<Array<String>>? = null

    /** Gallery image pick */
    private var galleryImagePickLauncher: ActivityResultLauncher<PickVisualMediaRequest>? = null

    /**
     * Security dialog
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val securityDialog by lazy { SecurityDialog() }

    /** Photo add dialog */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val photoAddEditDialog by lazy { PhotoAddToTaskDialog() }

    /** Photo show dialog */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val photoShowDialog by lazy { PhotoShowDialog() }

    /** Voice add Dialog */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val voiceAddEditDialog by lazy { VoiceAddToTaskDialog() }

    /** Voice add Dialog */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val galleryImagesDialog by lazy { GalleryImagesDialog() }

    /** Section choose dialog */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val sectionChooseDialog by lazy { SectionChooseDialog() }

    /**
     * Creation of fragment
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditTaskBinding.inflate(inflater, container, false)

        setActivityResultLauncher()
        setGalleryImagePickLauncher()
        getIcons()
        getSection()
        setupIconsTaskAdapter()
        setupSectionNameObserver()
        setupEventObserver()
        setupFields()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setFragmentResultListener()
    }

    /** Permissions of notification for alert */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value) permissionGranted = false
            }
            if (!permissionGranted) {
                // TODO: dialog to say why it's important
                Log.e("Error", "permissions are not granted!")
            } else {
                pickDateTime()
            }
        }
    }

    /** Gallery image pick */
    private fun setGalleryImagePickLauncher() {
        galleryImagePickLauncher = registerForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia()
        ) { listUri ->
            if (listUri.isNotEmpty()) {
                requireContext().contentResolver.let { contentResolver: ContentResolver ->
                    val readUriPermission: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    listUri.forEach { uri ->
                        contentResolver.takePersistableUriPermission(uri, readUriPermission)
                    }

                    // Add list of selected uri to existing list in ViewModel, not replace
                    viewModel.addListUriOfGalleryImages(listUri)
                }
            }
        }
    }

    /** Get icons to show in RecyclerView */
    private fun getIcons() {
        viewModel.getTaskIcons()
    }

    /** Init IconsTaskAdapter and observe elements from ViewModel */
    private fun setupIconsTaskAdapter() {
        iconsTaskAdapter = IconsTaskAdapter(viewModel.taskIcon) { element, position ->
            onGroupIconSelected(element, position)
        }

        binding.addEditTaskCategoryRecyclerview.apply {
            adapter = iconsTaskAdapter
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

    /** Fill IconsTaskAdapter with elements and choose which is selected */
    private fun onIconsGet(icons: List<IconTaskEntity>) {
        val selectedIcon = icons.first { it.groupId == viewModel.taskIcon }
        val indexOfSelectedIcon = icons.indexOf(selectedIcon)
        iconsTaskAdapter?.submitList(icons)
        val marginElements = if (indexOfSelectedIcon >= TWO) TWO else ZERO
        binding.addEditTaskCategoryRecyclerview.scrollToPosition(indexOfSelectedIcon - marginElements)
    }

    /** Observe and setup section name from ViewModel  */
    private fun setupSectionNameObserver() {
        viewModel.sectionName.observe(viewLifecycleOwner) { sectionTitle ->
            binding.addEditTaskSectionSelection.text = if(sectionTitle == EMPTY_STRING) {
                resources.getString(R.string.no_section)
            } else {
                sectionTitle
            }
        }
    }

    /** Setup fields value */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setupFields() {
        with(binding) {
            /** Title */
            viewModel.taskEntity?.let {
                addEditTaskTitle.setText(
                    checkForEmptyTitle(
                        viewModel.taskTitle, resources, viewModel.getTaskId()
                    )
                )
            } ?: kotlin.run { EMPTY_STRING }
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
            /** Alarm */
            addEditTaskIncludeAlarm.addEditTaskAlarmCheckbox.apply {
                this.isChecked = viewModel.taskAlarm != ZERO_LONG
                this.jumpDrawablesToCurrentState()
                isAlarmPickerShown()
            }
            addEditTaskIncludeAlarm.addEditTaskAlarmCheckbox.setOnCheckedChangeListener { _, isCheck ->
                viewModel.alarmIsOn = isCheck
                updateAlarmDateTimeText()
                isAlarmPickerShown(isCheck = isCheck)
            }
            addEditTaskIncludeAlarm.addEditTaskAlarmPickerDate.apply {
                updateAlarmDateTimeText()
                setOnClickListener {
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        pickDateTime()
                    } else {
                        if (hasPermissions(requireContext())) {
                            pickDateTime()
                        } else {
                            activityResultLauncher?.launch(REQUIRED_PERMISSIONS)
                        }
                    }
                }
            }
            /** Security password code */
            addEditTaskIncludeSecurity.addEditTaskSecurityCheckbox.apply {
                this.isChecked = viewModel.isPasswordSecurityTurnOn
                isSecurityPickerShown(isPasswordSecurityTurnOn = viewModel.isPasswordSecurityTurnOn)
                this.jumpDrawablesToCurrentState()
            }
            addEditTaskIncludeSecurity.addEditTaskSecurityCheckbox.setOnCheckedChangeListener { _, isCheck ->
                viewModel.isPasswordSecurityTurnOn = isCheck
                isSecurityPickerShown(isPasswordSecurityTurnOn = isCheck)
            }
            addEditTaskIncludeSecurity.addEditTaskSecurityCodePicker.apply {
                setOnClickListener {
                    val currentSecurityDialog = securityDialog.newInstance(
                        type = Security.Create(
                            place = Place.TaskPlace(taskId = viewModel.getTaskId() ?: viewModel.nextTaskId),
                            purpose = if(viewModel.isPasswordSecurityTurnOn) {
                                SecurityPurpose.UPDATE_TASK_PASSWORD
                            } else {
                                SecurityPurpose.CREATE_TASK_PASSWORD
                            }
                        ),
                    )
                    currentSecurityDialog.let { dialog ->
                        if (!dialog.isVisible) {
                            dialog.show(childFragmentManager, SecurityDialog::class.simpleName)
                        }
                    }
                }
            }
            /** Expired */
            addEditTaskIncludeIntervals.addEditTaskExpiredCheckbox.apply {
                this.isChecked = viewModel.isTaskExpired
                this.jumpDrawablesToCurrentState()
                isDatePickersShown()
            }
            addEditTaskIncludeIntervals.addEditTaskExpiredCheckbox.setOnCheckedChangeListener { _, isCheck ->
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
            /** Event flag */
            addEditTaskIncludeIntervals.addEditTaskDatePickerCalendarEvent.isChecked =
                viewModel.isEvent && !viewModel.isRange
            /** Range flag */
            addEditTaskIncludeIntervals.addEditTaskDatePickerCalendarRange.isChecked =
                viewModel.isRange && !viewModel.isEvent
            /** is Event (not range) listener */
            addEditTaskIncludeIntervals.addEditTaskDatePickerRadioGroup.setOnCheckedChangeListener { _, checkId ->
                when (checkId) {
                    binding.addEditTaskIncludeIntervals.addEditTaskDatePickerCalendarEvent.id -> {
                        viewModel.isEvent = true
                        viewModel.isRange = false
                    }

                    binding.addEditTaskIncludeIntervals.addEditTaskDatePickerCalendarRange.id -> {
                        viewModel.isEvent = false
                        viewModel.isRange = true
                    }

                    else -> {
                        Log.e("Error", "Error setting type: event / range.")
                    }
                }
                addEditTaskIncludeIntervals.addEditTaskDatePickerSecond.isVisible =
                    viewModel.isTaskExpired && !viewModel.isEvent && viewModel.isRange
            }
            /** First Date picker */
            addEditTaskIncludeIntervals.addEditTaskDatePickerFirst.setOnClickListener {
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
            addEditTaskIncludeIntervals.addEditTaskDatePickerSecond.isVisible =
                viewModel.isTaskExpired && !viewModel.isEvent && viewModel.isRange
            addEditTaskIncludeIntervals.addEditTaskDatePickerSecond.setOnClickListener {
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
            /** Galley images attach */
            addEditTaskGalleryPhoto.setOnClickListener {
                galleryImagePickLauncher?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            /** Photo Attached */
            addEditTaskPhoto.setOnClickListener {
                if (!photoAddEditDialog.isVisible) {
                    photoAddEditDialog.show(childFragmentManager, PhotoAddToTaskDialog::class.simpleName)
                }
            }
            /** Link Attached */
            addEditTaskLink.setOnClickListener {
                val linkAddDialog = LinkAddToTaskDialog().newInstance(
                    onLinkSaveAction = ::linkSave
                )
                if (!linkAddDialog.isVisible) {
                    linkAddDialog.show(childFragmentManager, LinkAddToTaskDialog::class.simpleName)
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
                    val dialog = LinkUrlChooseDialog().newInstance(
                        onLinkUrlClicked = ::urlSelected,
                        onLinkUrlDelete = ::urlDelete,
                        listLinks = viewModel.linkAttached.split(COMMA)
                            .filter { it.isNotEmpty() || it.isNotEmpty() }
                    )
                    if (!dialog.isVisible) {
                        dialog.show(childFragmentManager, LinkUrlChooseDialog::class.simpleName)
                    }
                }
            }
            /** Tag photo */
            addEditTaskPhotoTag.apply{
                setTagPhotoVisibility(viewModel.photoAttached.isNotEmpty())
                setOnClickListener {
                    if (!photoShowDialog.isVisible) {
                        photoShowDialog.show(
                            childFragmentManager,
                            PhotoShowDialog::class.simpleName
                        )
                    }
                }
            }
            /** Tag voice */
            addEditTaskVoiceTag.apply{
                setTagVoiceVisibility(viewModel.voiceAttached.isNotEmpty())
                setOnClickListener {
                    if (!voiceAddEditDialog.isVisible) {
                        voiceAddEditDialog.show(
                            childFragmentManager,
                            VoiceAddToTaskDialog::class.simpleName
                        )
                    }
                }
            }
            /** Tag gallery images */
            addEditTaskGalleryImagesTag.apply{
                setTagGalleryImagesVisibility(viewModel.attachedGalleryImages.isNotEmpty())
                setOnClickListener {
                    if (!galleryImagesDialog.isVisible) {
                        galleryImagesDialog.show(
                            childFragmentManager,
                            GalleryImagesDialog::class.simpleName
                        )
                    }
                }
            }
            /** Choose section (category of task) */
            addEditTaskSectionSelection.setOnClickListener {
                val dialog = sectionChooseDialog.newInstance()
                if (!dialog.isVisible) {
                    sectionChooseDialog.show(
                        childFragmentManager,
                        SectionChooseDialog::class.simpleName
                    )
                }
            }
            /** Check is something attached to task and set label about this */
            setLabelOfAttached()
            /** OK Button */
            addEditTaskOkButton.setOnClickListener {
                viewModel.saveTaskBtnClicked()
            }
        }
    }

    private fun pickDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE) + ONE

        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance().apply {
                    set(year, month, day, hour, minute)
                }
                viewModel.taskAlarm = pickedDateTime.time.time
                viewModel.alarmIsOn = true
                updateAlarmDateTimeText()
            }, startHour, startMinute, true).show()
        }, startYear, startMonth, startDay).also {
            it.datePicker.minDate = Date().time
        }.show()
    }

    private fun setFirstDateOfExpire() {
        binding.addEditTaskIncludeIntervals.addEditTaskDatePickerFirst.text =
            if (viewModel.taskExpireFirstDate == ZERO_LONG) {
                getStartDate(
                    year = getCurrentYear(),
                    month = getCurrentMonth(),
                    day = getCurrentDay()
                ).toFormattedDateString()
            } else {
                viewModel.taskExpireFirstDate.toFormattedDateString()
            }
    }

    private fun setSecondDateOfExpire() {
        binding.addEditTaskIncludeIntervals.addEditTaskDatePickerSecond.text =
            if (viewModel.taskExpireSecondDate == ZERO_LONG) {
                (getFinishDate(
                    year = getCurrentYear(),
                    month = getCurrentMonth(),
                    day = getCurrentDay()
                ) + ONE_DAY_MLS
                        ).toFormattedDateString()
            } else if (viewModel.taskExpireFirstDate == viewModel.taskExpireSecondDate) {
                /**
                 * User choose event type without changing dates, save.
                 * After change type to range and if start was today, finish will be tomorrow by auto,
                 * but if start was today + N days -> user have to reselect finish day manually.
                 * That's why after reselecting from event to range we can pass finish as tomorrow.
                 * But the best way to set finish date: start date + end of next day.
                 */
                viewModel.taskExpireSecondDate = getDefaultStartDateTime()
                viewModel.taskExpireSecondDate.toFormattedDateString()
            }
            else {
                viewModel.taskExpireSecondDate.toFormattedDateString()
            }
    }

    private fun onGroupIconSelected(element: IconTaskEntity, position: Int?) {
        position?.let {
            viewModel.taskIcon = element.groupId
            binding.addEditTaskCategoryRecyclerview.smoothScrollToPosition(it)
        }
    }

    private fun isDatePickersShown() {
        binding.apply {
            // Label
            addEditTaskIncludeIntervals.addEditTaskAddDateLabel.isVisible = !viewModel.isTaskExpired
            addEditTaskIncludeIntervals.addEditTaskAddDateLabelArrow.isVisible =
                !viewModel.isTaskExpired
            // Radio flags of event / radio
            addEditTaskIncludeIntervals.addEditTaskDatePickerCalendarEvent.isVisible =
                viewModel.isTaskExpired
            addEditTaskIncludeIntervals.addEditTaskDatePickerCalendarRange.isVisible =
                viewModel.isTaskExpired
            // Date pickers with date as title
            addEditTaskIncludeIntervals.addEditTaskDatePickerFirst.isVisible =
                viewModel.isTaskExpired && (viewModel.isRange || viewModel.isEvent)
            addEditTaskIncludeIntervals.addEditTaskDatePickerSecond.isVisible =
                viewModel.isTaskExpired && viewModel.isRange
        }
    }

    private fun isAlarmPickerShown(isCheck: Boolean? = null) {
        binding.apply {
            // Label
            addEditTaskIncludeAlarm.addEditTaskAlarmAddAlarmLabel.isVisible =
                isCheck?.not() ?: (viewModel.taskAlarm == ZERO_LONG)
            addEditTaskIncludeAlarm.addEditTaskAlarmLabelArrow.isVisible =
                isCheck?.not() ?: (viewModel.taskAlarm == ZERO_LONG)
            // Date picker with date as title for alarm
            addEditTaskIncludeAlarm.addEditTaskAlarmPickerDate.isVisible =
                isCheck ?: (viewModel.taskAlarm != ZERO_LONG)
        }
    }

    private fun isSecurityPickerShown(
        isPasswordSecurityTurnOn: Boolean
    ) {
        binding.apply {
            // Label
            addEditTaskIncludeSecurity.addEditTaskSecurityLabel.isVisible = isPasswordSecurityTurnOn.not()
            addEditTaskIncludeSecurity.addEditTaskSecurityLabelArrow.isVisible = isPasswordSecurityTurnOn.not()
            // Code picker
            addEditTaskIncludeSecurity.addEditTaskSecurityCodePicker.isVisible = isPasswordSecurityTurnOn
            if(isPasswordSecurityTurnOn && (viewModel.hasPasswordOnStart || viewModel.taskNewPassword != EMPTY_STRING)) {
                addEditTaskIncludeSecurity.addEditTaskSecurityCodePicker.text = getString(R.string.change_security_code)
            } else {
                addEditTaskIncludeSecurity.addEditTaskSecurityCodePicker.text = resources.getString(R.string.set_security_code)
            }
        }
    }

    private fun updateAlarmDateTimeText() {
        if (viewModel.taskAlarm != ZERO_LONG) {
            binding.addEditTaskIncludeAlarm.addEditTaskAlarmPickerDate.text =
                viewModel.taskAlarm.toFormattedDateTimeString()

            val color = if ((viewModel.taskAlarm) <= Date().time) {
                R.color.dark_red
            } else {
                R.color.main
            }
            binding.addEditTaskIncludeAlarm.addEditTaskAlarmPickerDate.setTextColor(
                resources.getColor(color)
            )
        }
    }

    /** Event: navigate to AllTasksFragment with result */
    private fun navigateToMainScreen(event: Int) {
        binding.addEditTaskTitle.clearFocus()
        findNavController().navigate(R.id.action_editTaskFragment_to_allTasksFragment2,
            bundleOf(EDIT_ADD_TASK_TO_ALL_TASKS_EVENT to event),
            navOptions {
                popUpTo(R.id.allTasksFragment) {
                    inclusive = true
                }
                anim {
                    enter = android.R.animator.fade_in
                    exit = android.R.animator.fade_out
                }
            }
        )
    }

    /** Event observer */
    private fun setupEventObserver() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.editTaskChannel.collect { event: EditTaskEventContract ->
                when (event) {
                    /** Navigation: Go back */
                    is EditTaskEventContract.NavBackWithResult -> {
                        navigateToMainScreen(event.typeNewOrEditorExist)
                    }

                    /** Error: Blank title text show */
                    is EditTaskEventContract.ShowErrorBlankTitleAndMessage -> {
                        getSnackBar(
                            resources.getString(R.string.snack_bar_empty_task_title_forbidden),
                            requireView(),
                        ).show()
                    }

                    /** Error: Dates picked show */
                    is EditTaskEventContract.ShowErrorDatesPicked -> {
                        getSnackBar(
                            resources.getString(R.string.snack_bar_dates_picked),
                            requireView(),
                        ).show()
                    }

                    /** Error: Task is not saved */
                    is EditTaskEventContract.TaskNotSaved -> {
                        getSnackBar(
                            resources.getString(R.string.snack_bar_task_not_saved),
                            requireView(),
                        ).show()
                    }

                    /** Error: Alarm time is in passed */
                    is EditTaskEventContract.ShowErrorAlarmTime -> {
                        getSnackBar(
                            resources.getString(R.string.snack_bar_error_alarm_time),
                            requireView(),
                        ).show()
                    }

                    /** Refresh tags to describe attached */
                    is EditTaskEventContract.RefreshTagsVisibility -> {
                        setLabelOfAttached()
                        setTagUrlVisibility(event.url)
                        setTagPhotoVisibility(event.photo)
                        setTagVoiceVisibility(event.voice)
                        setTagGalleryImagesVisibility(event.galleryImages)
                    }

                    /** Refresh view of password add/edit after new password set */
                    is EditTaskEventContract.OnSuccessPasswordCreateOrUpdate -> {
                        isSecurityPickerShown(isPasswordSecurityTurnOn = true)
                    }
                }
            }
        }
    }

    private fun onSectionSelect(section: Section) {
        viewModel.sectionId = section.id
        getSection()
    }

    private fun getSection() {
        viewModel.getSectionById()
    }

    /** TAGS */

    private fun setTagUrlVisibility(value: Boolean) {
        binding.addEditTaskUrlTag.isVisible = value
    }

    private fun setTagPhotoVisibility(value: Boolean) {
        binding.addEditTaskPhotoTag.isVisible = value
    }

    private fun setTagVoiceVisibility(value: Boolean) {
        binding.addEditTaskVoiceTag.isVisible = value
    }

    private fun setTagGalleryImagesVisibility(value: Boolean) {
        binding.addEditTaskGalleryImagesTag.isVisible = value
    }

    /** Show label of attach instead of tags */
    private fun setLabelOfAttached() {
        binding.addEditTaskAttachedNothingLabel.visibility =
            if (viewModel.attachedGalleryImages.isEmpty() &&
                viewModel.voiceAttached.isEmpty() &&
                viewModel.photoAttached.isEmpty() &&
                viewModel.linkAttached.isEmpty()
            ) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
    }

    /**
     * Add link to list of links
     */
    private fun linkSave(linkUrl: String) {
        viewModel.linkAttached = viewModel.linkAttached + linkUrl.trim() + COMMA
    }

    /**
     * Open webview for link
     */
    private fun urlSelected(linkUrl: String) {
        findNavController().navigate(
            AddEditTaskFragmentDirections.actionGlobalUrlWebView(linkUrl)
        )
    }

    /**
     * Delete link from list of links
     */
    private fun urlDelete(linkUrl: String) {
        viewModel.linkAttached = viewModel.linkAttached
            .split(COMMA)
            .filter { it != linkUrl }
            .joinToString(COMMA)
    }

    /**
     * Listener for [SecurityDialog], [SectionChooseDialog]
     */
    private fun setFragmentResultListener() {
        setFragmentResultListener(SUCCESS_CHOOSE_SECTION_RESULT) { _, bundle ->
            val section = bundle.get(BUNDLE_SECTION_CHOOSE) as Section
            onSectionSelect(section)
        }
        setFragmentResultListener(SUCCESS_ADD_PASSWORD_RESULT) { _, bundle ->
            // Task password should be create or update
            bundle.getBoolean(BUNDLE_IS_PASSWORD_CREATION).also { isPasswordCreation ->
                viewModel.isPasswordCreation = isPasswordCreation
            }

            // Task new password
            val password = bundle.getString(BUNDLE_PASSWORD_VALUE).also { password ->
                password?.let {
                    viewModel.taskNewPassword = password
                } ?: kotlin.run {
                    Log.e("Error", "password after creation in security dialog get null")
                }
            }

            viewModel.isPasswordSecurityTurnOn = true
        }
    }

    override fun onDestroyView() {
        iconsTaskAdapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun hasPermissions(context: Context) = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private val REQUIRED_PERMISSIONS = mutableListOf(
            android.Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.USE_EXACT_ALARM,
            android.Manifest.permission.SCHEDULE_EXACT_ALARM,
        ).toTypedArray()
    }
}
