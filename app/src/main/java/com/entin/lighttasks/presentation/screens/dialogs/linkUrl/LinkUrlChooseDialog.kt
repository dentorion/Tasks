package com.entin.lighttasks.presentation.screens.dialogs.linkUrl

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.LinkUrlDialogBinding
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.isOrientationLandscape
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LinkUrlChooseDialog : DialogFragment() {

    private var _binding: LinkUrlDialogBinding? = null
    private val binding get() = _binding!!

    private var linkUrlAdapter: LinkUrlAdapter? = LinkUrlAdapter(
        onClick = ::openLink,
        onDelete = ::deleteLink
    )
    private var onSelect: ((String) -> Unit)? = null
    private var onDelete: ((String) -> Unit)? = null
    private var listLink: List<String>? = null

    fun newInstance(
        onLinkUrlClicked: (String) -> Unit,
        onLinkUrlDelete: (String) -> Unit,
        listLinks: List<String>
    ): LinkUrlChooseDialog =
        LinkUrlChooseDialog().apply {
            this.onSelect = onLinkUrlClicked
            this.onDelete = onLinkUrlDelete
            this.listLink = listLinks
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

        setupLinkUrlRecyclerView()
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
        isCancelable = true
        _binding = LinkUrlDialogBinding.inflate(inflater, container, false)

        binding.sectionChooseClose.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    private fun setupLinkUrlRecyclerView() {
        binding.sectionChooseRecyclerView.apply {
            adapter = linkUrlAdapter
            hasFixedSize()
            setItemViewCacheSize(ZERO)
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false,
            )
        }

        linkUrlAdapter?.submitList(listLink)
    }

    private fun openLink(linkUrl: String) {
        onSelect?.let { it(linkUrl) }
        dismiss()
    }

    private fun deleteLink(linkUrl: String) {
        onDelete?.let { it(linkUrl) }
        dismiss()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val FULL_SCREEN = 0.92
        const val LANDSCAPE_MODE = 0.65
    }
}
