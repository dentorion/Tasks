package com.entin.lighttasks.presentation.screens.dialogs

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import coil.load
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.ShowImageDialogBinding
import com.entin.lighttasks.presentation.util.SHOW_IMAGE_DIALOG_ARGUMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ShowImageDialog : DialogFragment() {

    private var _binding: ShowImageDialogBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null

    fun newInstance(imageUri: String): ShowImageDialog? {
        val dialog = ShowImageDialog()
        dialog.arguments = bundleOf(SHOW_IMAGE_DIALOG_ARGUMENT to imageUri)
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUri = Uri.parse(arguments?.getString(SHOW_IMAGE_DIALOG_ARGUMENT))
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dialog ->
            dialog.window?.apply {
                attributes?.windowAnimations = R.style.NoPaddingDialogTheme
                setGravity(Gravity.CENTER)
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.ShowImageDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ShowImageDialogBinding.inflate(inflater, container, false)

        with(binding) {
            dialogShowImage.load(imageUri)
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
