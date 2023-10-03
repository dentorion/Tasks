package com.entin.lighttasks.presentation.screens.dialogs

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import coil.load
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.PhotoShowDialogBinding
import com.entin.lighttasks.presentation.screens.addedit.AddEditTaskViewModel
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.core.PhotoMaker
import com.entin.lighttasks.presentation.util.isOrientationLandscape
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PhotoShowDialog : DialogFragment() {

    private var _binding: PhotoShowDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddEditTaskViewModel by viewModels(ownerProducer = { requireParentFragment() })

    @Inject
    lateinit var photoMaker: PhotoMaker

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
        _binding = PhotoShowDialogBinding.inflate(inflater, container, false)

        with(binding) {
            /** Show Photo */
            photoMaker.showPhoto(viewModel.photoAttached) { internalUri: Uri ->
                binding.dialogPhotoShow.load(internalUri) {
                    crossfade(true)
                }
            }

            /** Close */
            dialogPhotoShowCancelButton.setOnClickListener {
                dismiss()
            }

            /** Delete photo */
            dialogPhotoShowDeletePhotoButton.setOnClickListener {
                photoMaker.deleteImage(viewModel.photoAttached)
                viewModel.photoAttached = EMPTY_STRING
                dismiss()
            }
        }
        return binding.root
    }

    private fun setDialogWidthHeight(width: Double, height: Double) {
        val newWidth = (resources.displayMetrics.widthPixels * width).toInt()
        val newHeight = (resources.displayMetrics.heightPixels * height).toInt()

        dialog?.window?.setLayout(newWidth, newHeight)
    }

    companion object {
        const val WIDTH_FULL_SCREEN = 0.8
        const val WIDTH_LANDSCAPE_MODE = 0.72
        const val HEIGHT_FULL_SCREEN = 0.8
        const val HEIGHT_LANDSCAPE_MODE = 0.72
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
