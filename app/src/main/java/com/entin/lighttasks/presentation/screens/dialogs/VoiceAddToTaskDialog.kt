package com.entin.lighttasks.presentation.screens.dialogs

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.VoiceAttachedDialogBinding
import com.entin.lighttasks.presentation.screens.addedit.AddEditTaskViewModel
import com.entin.lighttasks.presentation.util.core.AudioPlayer
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.core.SoundRecorder
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.isOrientationLandscape
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class VoiceAddToTaskDialog : DialogFragment() {

    private var _binding: VoiceAttachedDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddEditTaskViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var fileNameToSaveInTask = EMPTY_STRING
    private val commonState = MediatorLiveData(CommonState())

    /** Permissions */
    private var activityResultLauncher: ActivityResultLauncher<Array<String>>? = null

    @Inject
    lateinit var soundRecorder: SoundRecorder

    @Inject
    lateinit var audioPlayer: AudioPlayer

    private fun setDialogWidth(width: Double) {
        val newWidth = (resources.displayMetrics.widthPixels * width).toInt()
        dialog?.window?.setLayout(newWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun hideSystemUI() {
        dialog?.let { dialog ->
            dialog.window?.let { window ->
                view?.let { view ->
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    WindowInsetsControllerCompat(window, view).systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()

        fileNameToSaveInTask = viewModel.voiceAttached
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dialog ->
            dialog.window?.apply {
                attributes?.windowAnimations = R.style.NoPaddingDialogTheme
                setGravity(Gravity.CENTER)
            }
            setDialogWidth(if (isOrientationLandscape(context)) LANDSCAPE_MODE else FULL_SCREEN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        isCancelable = false
        _binding = VoiceAttachedDialogBinding.inflate(inflater, container, false)

        setActivityResultLauncher()

        if (!hasPermissions(requireContext())) {
            activityResultLauncher?.launch(REQUIRED_PERMISSIONS)
        }

        commonState.addSource(soundRecorder.recordState) { value ->
            audioPlayer.stop()
            commonState.value = commonState.value?.copy(
                isRecording = value.isRecording,
                timer = value.timer,
                fileName = value.fileName,
                isPlaying = if(value.isRecording) false else commonState.value!!.isPlaying,
                isPause = if(value.isRecording) false else commonState.value!!.isPause,
                isStop = if(value.isRecording) false else commonState.value!!.isStop,
            )
        }
        commonState.addSource(audioPlayer.playState) { value ->
            commonState.value = commonState.value?.copy(
                isPlaying = value.isPlaying,
                isPause = value.isPause,
                isStop = value.isStop,
                progress = value.progress,
            )
        }

        commonState.observe(viewLifecycleOwner) { commonState ->
            binding.apply {
                /** Timer indicator */
                dialogVoiceAttachedTimer.text = commonState.timer
                val color = if (commonState.isRecording) R.color.dark_red else R.color.rose
                dialogVoiceAttachedTimer.setTextColor(resources.getColor(color))

                /** Record button */
                binding.dialogVoiceAttachedRecordButton.setColorFilter(getColor(requireContext(), color))

                /** Get file name to save in task */
                if(commonState.fileName != EMPTY_STRING) {
                    fileNameToSaveInTask = commonState.fileName
                }

                /** Play button */
                dialogVoiceAttachedPlayButton.setColorFilter(
                    getColor(requireContext(), getPlayerButtonsColor(ButtonType.PLAY, commonState))
                )

                /** Pause button */
                dialogVoiceAttachedPauseButton.setColorFilter(
                    getColor(requireContext(), getPlayerButtonsColor(ButtonType.PAUSE, commonState))
                )

                /** Stop button */
                dialogVoiceAttachedStopButton.setColorFilter(
                    getColor(requireContext(), getPlayerButtonsColor(ButtonType.STOP, commonState))
                )

                /** ProgressBar */
                dialogVoiceAttachedProgress.progress = commonState.progress
            }
        }

        with(binding) {
            /** Start recording */
            dialogVoiceAttachedRecordButton.setOnClickListener {
                if (!hasPermissions(requireContext())) {
                    activityResultLauncher?.launch(REQUIRED_PERMISSIONS)
                } else {
                    if (soundRecorder.recordState.value?.isRecording == true) {
                        soundRecorder.stopRecording()
                        buttonsCloseSaveVisibility(isActive = true)
                    } else {
                        soundRecorder.startRecording()
                        buttonsCloseSaveVisibility(isActive = false)
                    }
                }
            }

            /** Play sound */
            binding.dialogVoiceAttachedPlayButton.setOnClickListener {
                if (commonState.value?.isRecording == false && fileNameToSaveInTask != EMPTY_STRING && commonState.value?.isPlaying == false) {
                    audioPlayer.play(fileNameToSaveInTask)
                }
            }
            /** Pause sound */
            binding.dialogVoiceAttachedPauseButton.setOnClickListener {
                if (commonState.value?.isRecording == false && commonState.value?.isPlaying == true) {
                    audioPlayer.pause()
                }
            }
            /** Stop sound */
            binding.dialogVoiceAttachedStopButton.setOnClickListener {
                if (commonState.value?.isRecording == false) {
                    if(commonState.value?.isPlaying == true || commonState.value?.isPause == true) {
                        audioPlayer.stop()
                    }
                }
            }

            /** Close dialog */
            dialogVoiceAttachedCancelButton.setOnClickListener {
                commonState.removeSource(soundRecorder.recordState)
                commonState.removeSource(audioPlayer.playState)
                soundRecorder.clear()
                audioPlayer.clear()
                fileNameToSaveInTask = EMPTY_STRING
                dismiss()
            }
            /** Save */
            dialogVoiceAttachedSaveButton.setOnClickListener {
                viewModel.voiceAttached = fileNameToSaveInTask
                commonState.removeSource(soundRecorder.recordState)
                commonState.removeSource(audioPlayer.playState)
                soundRecorder.clear()
                audioPlayer.clear()
                fileNameToSaveInTask = EMPTY_STRING
                dismiss()
            }
        }

        return binding.root
    }

    private fun getPlayerButtonsColor(buttonType: ButtonType, commonState: CommonState): Int {
        val fieldToCheck = when(buttonType) {
            ButtonType.PLAY -> commonState.isPlaying
            ButtonType.PAUSE -> commonState.isPause
            ButtonType.STOP -> commonState.isStop
        }
        return if (fieldToCheck) {
            R.color.dark_red
        } else if (commonState.isRecording) {
            R.color.gray_light
        } else {
            R.color.rose
        }
    }

    private fun buttonsCloseSaveVisibility(isActive: Boolean) {
        binding.apply {
            dialogVoiceAttachedCancelButton.isVisible = isActive
            dialogVoiceAttachedSaveButton.isVisible = isActive
        }
    }

    private fun setActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value) permissionGranted = false
            }
            if (!permissionGranted) {
                //TODO: Show dialog information about need permissions
                Log.e("Global", "VOICE PERMISSIONS ERROR")
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        commonState.removeSource(soundRecorder.recordState)
        commonState.removeSource(audioPlayer.playState)
        soundRecorder.clear()
        audioPlayer.clear()
        fileNameToSaveInTask = EMPTY_STRING
        super.onDestroyView()
    }

    companion object {
        const val FULL_SCREEN = 0.92
        const val LANDSCAPE_MODE = 0.65

        private val REQUIRED_PERMISSIONS =
            mutableListOf(android.Manifest.permission.RECORD_AUDIO).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        fun hasPermissions(context: Context) = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        enum class ButtonType {
            PLAY, PAUSE, STOP
        }

        data class CommonState(
            val isPlaying: Boolean = false,
            val isPause: Boolean = false,
            val isStop: Boolean = false,
            val progress: Int = ZERO,
            val isRecording: Boolean = false,
            val timer: String = "00:00",
            val fileName: String = EMPTY_STRING,
        )
    }
}
