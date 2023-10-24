package com.entin.lighttasks.presentation.screens.dialogs

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.LinkAttachedDialogBinding
import com.entin.lighttasks.presentation.screens.dialogs.linkUrl.LinkUrlChooseDialog
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.isOrientationLandscape
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LinkAddToTaskDialog : DialogFragment() {

    private var _binding: LinkAttachedDialogBinding? = null
    private val binding get() = _binding!!

    private var onLinkSave: ((String) -> Unit)? = null

    fun newInstance(onLinkSaveAction: (String) -> Unit): LinkAddToTaskDialog =
        LinkAddToTaskDialog().apply {
            this.onLinkSave = onLinkSaveAction
        }

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
            setDialogWidth(if (isOrientationLandscape(context)) LinkUrlChooseDialog.LANDSCAPE_MODE else LinkUrlChooseDialog.FULL_SCREEN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        isCancelable = false
        _binding = LinkAttachedDialogBinding.inflate(inflater, container, false)

        with(binding) {
            /** Set value of url from viewModel */
            linkAttachedValue.setText(EMPTY_STRING)

            /** Close dialog */
            dialogLinkAttachedCancelButton.setOnClickListener {
                dismiss()
            }

            /** Save */
            dialogLinkAttachedSaveButton.setOnClickListener {
                if (linkAttachedValue.text.toString().isNotEmpty() &&
                    linkAttachedValue.text.toString().isNotBlank()
                ) {
                    linkAttachedValueExample.visibility = View.INVISIBLE
                    linkAttachedValueIncorrect.visibility = View.INVISIBLE
                    onLinkSave?.let { action -> action(linkAttachedValue.text.toString()) }
                    dismiss()
                } else {
                    linkAttachedValueExample.visibility = View.INVISIBLE
                    linkAttachedValueIncorrect.visibility = View.VISIBLE
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
