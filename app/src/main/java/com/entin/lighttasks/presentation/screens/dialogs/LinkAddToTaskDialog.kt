package com.entin.lighttasks.presentation.screens.dialogs

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.LinkAttachedDialogBinding
import com.entin.lighttasks.presentation.screens.addedit.AddEditTaskViewModel
import com.entin.lighttasks.presentation.screens.addedit.EditTaskFragmentDirections
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.isOrientationLandscape
import com.entin.lighttasks.presentation.util.isUrlLink
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LinkAddToTaskDialog : DialogFragment() {

    private var _binding: LinkAttachedDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddEditTaskViewModel by viewModels(ownerProducer = { requireParentFragment() })

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        isCancelable = false
        _binding = LinkAttachedDialogBinding.inflate(inflater, container, false)

        with(binding) {
            /** Set value of url from viewModel */
            linkAttachedValue.setText(viewModel.linkAttached)

            /** Show OPEN button */
            dialogLinkAttachedOpenButton.apply {
                isVisible = linkAttachedValue.text.toString().isUrlLink()
                setOnClickListener {
                    findNavController().navigate(
                        EditTaskFragmentDirections.actionGlobalUrlWebView(viewModel.linkAttached)
                    )
                }
            }

            /** Close dialog */
            dialogLinkAttachedCancelButton.setOnClickListener {
                linkAttachedValue.setText(viewModel.linkAttached)
                dismiss()
            }

            /** Save */
            dialogLinkAttachedSaveButton.setOnClickListener {
                if (linkAttachedValue.text.toString().isUrlLink() ||
                    linkAttachedValue.text.toString().isEmpty()
                ) {
                    linkAttachedValueExample.visibility = View.INVISIBLE
                    linkAttachedValueIncorrect.visibility = View.INVISIBLE
                    viewModel.linkAttached = linkAttachedValue.text.toString()
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
