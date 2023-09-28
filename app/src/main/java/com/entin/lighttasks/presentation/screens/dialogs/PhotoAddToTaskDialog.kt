package com.entin.lighttasks.presentation.screens.dialogs

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.PhotoAttachedDialogBinding
import com.entin.lighttasks.presentation.screens.addedit.AddEditTaskViewModel
import com.entin.lighttasks.presentation.util.ImageCache
import com.entin.lighttasks.presentation.util.isOrientationLandscape
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PhotoAddToTaskDialog : DialogFragment() {

    private var _binding: PhotoAttachedDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddEditTaskViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private lateinit var cameraController: LifecycleCameraController
    private var buttonTakePhotoMakesPhotoAndSave: Boolean = false

    @Inject
    lateinit var imageCache: ImageCache

    /** Permissions */
    private var activityResultLauncher: ActivityResultLauncher<Array<String>>? = null

    override fun onStart() {
        super.onStart()
        dialog?.let { dialog ->
            dialog.window?.apply {
                attributes?.windowAnimations = R.style.NoPaddingDialogTheme
                setGravity(Gravity.CENTER)
            }
            setDialogWidthHeight(
                if (isOrientationLandscape(context)) WIDTH_LANDSCAPE_MODE else WIDTH_FULL_SCREEN,
                if (isOrientationLandscape(context)) HEIGHT_LANDSCAPE_MODE else HEIGHT_FULL_SCREEN
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        isCancelable = false
        _binding = PhotoAttachedDialogBinding.inflate(inflater, container, false)

        setActivityResultLauncher()

        with(binding) {
            /** Close */
            dialogPhotoAttachedCancelButton.setOnClickListener {
                buttonTakePhotoMakesPhotoAndSave = false
                dismiss()
            }

            /** Take photo */
            if (hasPermissions(requireContext())) {
                startCamera()
            } else {
                activityResultLauncher?.launch(REQUIRED_PERMISSIONS)
            }

            dialogPhotoAttachedTakePhotoButton.setOnClickListener {
                if (!hasPermissions(requireContext())) {
                    activityResultLauncher?.launch(REQUIRED_PERMISSIONS)
                } else {
                    if (buttonTakePhotoMakesPhotoAndSave) {
                        takePhoto()
                    } else {
                        startCamera()
                    }
                }
            }
        }
        return binding.root
    }

    /** Run camera view */
    private fun startCamera() {
        buttonTakePhotoMakesPhotoAndSave = true
        val previewView = binding.dialogPhotoAttachedViewFinder
        cameraController = LifecycleCameraController(requireContext()).apply {
            bindToLifecycle(this@PhotoAddToTaskDialog)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
        previewView.controller = cameraController
    }

    /** Take photo and after save it to task */
    private fun takePhoto() {
        cameraController.takePicture(ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    when (image.format) {
                        ImageFormat.JPEG, ImageFormat.DEPTH_JPEG -> {
                            onSuccessPhoto(image)
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("CAMERA", "Can't take photo: ${exception.message}", exception)
                }
            })
    }

    /** Photo has been taken and now save to the internal storage */
    private fun onSuccessPhoto(image: ImageProxy) {
        // Delete prev image if exist
        if (viewModel.photoAttached.isNotEmpty()) {
            imageCache.deleteImage(viewModel.photoAttached)
        }
        // Save new image tom internal storage
        imageCache.saveImage(
            imageName = imageCache.generateImageName(),
            image = image,
            dismiss = { dismiss() },
            setNameToTask = { name -> viewModel.photoAttached = name }
        )
    }

    private fun setDialogWidthHeight(width: Double, height: Double) {
        val newWidth = (resources.displayMetrics.widthPixels * width).toInt()
        val newHeight = (resources.displayMetrics.heightPixels * height).toInt()

        dialog?.window?.setLayout(newWidth, newHeight)
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
                buttonTakePhotoMakesPhotoAndSave = false
                //TODO: Show dialog information about need permissions
                Log.e("CAMERA", "ERROR")
            } else {
                startCamera()
            }
        }
    }

    companion object {
        const val WIDTH_FULL_SCREEN = 0.8
        const val WIDTH_LANDSCAPE_MODE = 0.72
        const val HEIGHT_FULL_SCREEN = 0.8
        const val HEIGHT_LANDSCAPE_MODE = 0.72

        private val REQUIRED_PERMISSIONS = mutableListOf(android.Manifest.permission.CAMERA).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

        fun hasPermissions(context: Context) = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onDestroyView() {
        activityResultLauncher = null
        _binding = null
        super.onDestroyView()
    }
}
