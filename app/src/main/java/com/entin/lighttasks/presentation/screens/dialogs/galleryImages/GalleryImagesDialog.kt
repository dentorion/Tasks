package com.entin.lighttasks.presentation.screens.dialogs.galleryImages

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.GalleryImagesAttachedDialogBinding
import com.entin.lighttasks.presentation.util.isOrientationLandscape
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class GalleryImagesDialog(
    private val taskId: Int?
) : DialogFragment() {

    private var _binding: GalleryImagesAttachedDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GalleryImagesViewModel by viewModels()

    private var galleryImagesAdapter: GalleryImagesAdapter? = null

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
        _binding = GalleryImagesAttachedDialogBinding.inflate(inflater, container, false)

        createAdapter()
        getAttachedImages()
        stateObserver()

        with(binding) {
            dialogVoiceAttachedCancelButton.setOnClickListener {
                dismiss()
            }
        }

        return binding.root
    }

    private fun createAdapter() {
        galleryImagesAdapter = GalleryImagesAdapter { imageClickedUri ->
            imageClicked(imageClickedUri)
        }

        binding.galleryImagesRecyclerview.apply {
            adapter = galleryImagesAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun getAttachedImages() {
        taskId?.let {
            viewModel.getTaskAttachedImages(it)
        }
    }

    private fun stateObserver() {
        viewModel.galleryImagesChannel.observe(viewLifecycleOwner) { listUri ->
            galleryImagesAdapter?.submitList(listUri)
        }
    }

    private fun imageClicked(imageClickedUri: Uri) {
        taskId?.let {
//            viewModel.deleteUri(it, imageClickedUri)
        }
    }

    override fun onDestroyView() {
        galleryImagesAdapter = null

        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val FULL_SCREEN = 0.92
        const val LANDSCAPE_MODE = 0.65
    }
}
