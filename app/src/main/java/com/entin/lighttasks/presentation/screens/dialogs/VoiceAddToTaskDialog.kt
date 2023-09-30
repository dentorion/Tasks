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
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.VoiceAttachedDialogBinding
import com.entin.lighttasks.presentation.screens.addedit.AddEditTaskViewModel
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.VoiceCache
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

    /** Permissions */
    private var activityResultLauncher: ActivityResultLauncher<Array<String>>? = null

    @Inject
    lateinit var voiceCache: VoiceCache

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

        /** Observe state of voice recorder */
        voiceCache.recordState.observe(viewLifecycleOwner) { state ->
            /** Record button */
            binding.dialogVoiceAttachedRecordButton.setColorFilter(
                getColor(requireContext(), getRecordButtonColor(state.isRecording))
            )
            /** Timer indicator */
            binding.dialogVoiceAttachedTimer.text = state.timer
            /** Get file name to save in task */
            fileNameToSaveInTask = state.fileName ?: EMPTY_STRING
            /** Buttons: play, pause, stop enable */
            if (!state.isRecording && !state.fileName.isNullOrEmpty()) {
                binding.dialogVoiceAttachedPlayButton.setColorFilter(
                    getColor(requireContext(), R.color.rose)
                )
                binding.dialogVoiceAttachedPauseButton.setColorFilter(
                    getColor(requireContext(), R.color.rose)
                )
                binding.dialogVoiceAttachedStopButton.setColorFilter(
                    getColor(requireContext(), R.color.rose)
                )
            } else {
                binding.dialogVoiceAttachedPlayButton.setColorFilter(
                    getColor(requireContext(), R.color.gray_light)
                )
                binding.dialogVoiceAttachedPauseButton.setColorFilter(
                    getColor(requireContext(), R.color.gray_light)
                )
                binding.dialogVoiceAttachedStopButton.setColorFilter(
                    getColor(requireContext(), R.color.gray_light)
                )
            }
            /** Timer indicator color */
            val color = if (state.isRecording) R.color.dark_red else R.color.rose
            binding.dialogVoiceAttachedTimer.setTextColor(resources.getColor(color))
        }

        with(binding) {
            /** Start recording */
            dialogVoiceAttachedRecordButton.setOnClickListener {
                if (!hasPermissions(requireContext())) {
                    activityResultLauncher?.launch(REQUIRED_PERMISSIONS)
                } else {
                    if (voiceCache.recordState.value?.isRecording == true) {
                        voiceCache.stopRecording()
                        buttonsCloseSave(isActive = true)
                    } else {
                        voiceCache.startRecording()
                        buttonsCloseSave(isActive = false)
                    }
                }
            }
            /** Close dialog */
            dialogVoiceAttachedCancelButton.setOnClickListener {
                voiceCache.clear()
                dismiss()
            }
            /** Save */
            dialogVoiceAttachedSaveButton.setOnClickListener {
                viewModel.voiceAttached = fileNameToSaveInTask
                voiceCache.clear()
                dismiss()
            }
        }
        return binding.root
    }

    private fun buttonsCloseSave(isActive: Boolean) {
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

    private fun getRecordButtonColor(isRecording: Boolean): Int = if (isRecording) {
        R.color.dark_red
    } else {
        R.color.rose
    }

    override fun onDestroyView() {
        _binding = null
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
    }
}
