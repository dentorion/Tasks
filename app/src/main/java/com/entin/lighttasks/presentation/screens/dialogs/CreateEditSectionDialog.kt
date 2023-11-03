package com.entin.lighttasks.presentation.screens.dialogs

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.entin.lighttasks.R
import com.entin.lighttasks.data.db.entity.IconTaskEntity
import com.entin.lighttasks.databinding.CreateEditSectionDialogBinding
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.presentation.screens.addedit.adapter.IconsTaskAdapter
import com.entin.lighttasks.presentation.screens.addedit.adapter.SlowlyLinearLayoutManager
import com.entin.lighttasks.presentation.screens.section.SectionViewModel
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.ZERO_LONG
import com.entin.lighttasks.presentation.util.isOrientationLandscape
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.Date

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CreateEditSectionDialog : DialogFragment() {

    private var _binding: CreateEditSectionDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SectionViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var iconAdapter: IconsTaskAdapter? = null
    private lateinit var sectionForSave: Section
    private var isUpdate: Boolean = false

    fun newInstance(
        section: Section? = null,
        isUpdate: Boolean = section != null
    ): CreateEditSectionDialog =
        CreateEditSectionDialog().apply {
            this.sectionForSave = section ?: Section(
                title = EMPTY_STRING,
                createdAt = Date().time,
                editedAt = ZERO_LONG,
                icon = ZERO,
                isImportant = false,
                position = ZERO,
                hasPassword = false
            )
            this.isUpdate = isUpdate
            this.arguments = bundleOf(
                SECTION to this.sectionForSave,
                IS_UPDATE to this.isUpdate
            )
        }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(SECTION, sectionForSave)
        outState.putBoolean(IS_UPDATE, isUpdate)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (savedInstanceState != null) {
            this.sectionForSave = arguments?.get(SECTION) as Section
            this.isUpdate = arguments?.getBoolean(IS_UPDATE) as Boolean
        }

        isCancelable = false
        _binding = CreateEditSectionDialogBinding.inflate(inflater, container, false)

        setupIconRecyclerView()
        viewModel.getIcons()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.iconTaskChannel.collect { icons ->
                    onIconsGet(icons)
                }
            }
        }

        return binding.root
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

    override fun onResume() {
        super.onResume()
        hideSystemUI()

        with(binding) {
            /** Title */
            dialogAddEditSectionTitleValue.text =
                SpannableStringBuilder(sectionForSave.title)

            /** Important */
            dialogAddEditSectionImportantCheckbox.apply {
                this.isChecked = sectionForSave?.isImportant ?: false
                this.jumpDrawablesToCurrentState()
                setOnCheckedChangeListener { _, isCheck ->
                    sectionForSave = sectionForSave.copy(isImportant = isCheck)
                }
            }

            /** Close dialog */
            dialogAddEditSectionCancelButton.setOnClickListener {
                dismiss()
            }

            /** Save */
            dialogAddEditSectionSaveButton.setOnClickListener {
                if (dialogAddEditSectionTitleValue.text?.isNotEmpty() == true &&
                    dialogAddEditSectionTitleValue.text?.isNotBlank() == true
                ) {

                    dialogAddEditSectionValueIncorrect.isVisible = false
                    sectionForSave = sectionForSave.copy(
                        title = dialogAddEditSectionTitleValue.text.toString()
                    )
                    viewModel.onSaveButtonClick(section = sectionForSave, isUpdate = isUpdate)
                    dismiss()
                } else {
                    dialogAddEditSectionValueIncorrect.isVisible = true
                }
            }
        }
    }

    /**
     * Icon
     */
    private fun setupIconRecyclerView() {
        iconAdapter = IconsTaskAdapter(sectionForSave?.icon ?: ZERO) { element, position ->
            onGroupIconSelected(element, position)
        }

        binding.dialogAddEditSectionCategoryRecyclerview.apply {
            adapter = iconAdapter
            layoutManager = SlowlyLinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false,
            )
        }
    }

    /**
     * On icon selected
     */
    private fun onGroupIconSelected(element: IconTaskEntity, position: Int?) {
        position?.let {
            sectionForSave = sectionForSave.copy(icon = element.groupId)
            binding.dialogAddEditSectionCategoryRecyclerview.smoothScrollToPosition(it)
        }
    }

    /**
     * On list of icons get
     */
    private fun onIconsGet(icons: List<IconTaskEntity>) {
        val selectedIcon = icons.first { it.groupId == sectionForSave.icon }
        val indexOfSelectedIcon = icons.indexOf(selectedIcon)
        iconAdapter?.submitList(icons)
        val marginElements = if (indexOfSelectedIcon >= 2) 2 else 0
        binding.dialogAddEditSectionCategoryRecyclerview.scrollToPosition(indexOfSelectedIcon - marginElements)
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

    override fun onDestroyView() {
        iconAdapter = null
        binding.dialogAddEditSectionCategoryRecyclerview.adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val FULL_SCREEN = 0.92
        const val LANDSCAPE_MODE = 0.65

        const val SECTION = "SECTION"
        const val IS_UPDATE = "IS_UPDATE"
    }
}
