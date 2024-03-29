package com.entin.lighttasks.presentation.screens.section

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.SectionPreferencesBinding
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.presentation.screens.dialogs.createEditSection.CreateEditSectionDialog
import com.entin.lighttasks.presentation.screens.dialogs.deleteSection.DeleteSectionDialog
import com.entin.lighttasks.presentation.screens.dialogs.security.Place
import com.entin.lighttasks.presentation.screens.dialogs.security.Security
import com.entin.lighttasks.presentation.screens.dialogs.security.SecurityPurpose
import com.entin.lighttasks.presentation.screens.dialogs.security.SecurityDialog
import com.entin.lighttasks.presentation.screens.section.adapter.SectionPreferencesAdapter
import com.entin.lighttasks.presentation.screens.section.adapter.SectionTouchHelperCallback
import com.entin.lighttasks.presentation.util.BUNDLE_PASSWORD_RESULT_SECURITY_TYPE
import com.entin.lighttasks.presentation.util.SUCCESS_CHECK_PASSWORD_RESULT
import com.entin.lighttasks.presentation.util.ZERO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class SectionFragment : Fragment(R.layout.section_preferences) {

    private var _binding: SectionPreferencesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SectionViewModel by viewModels()
    private var sectionPreferencesAdapter: SectionPreferencesAdapter? = SectionPreferencesAdapter(
        onEdit = ::openEditSectionDialog,
        onDelete = ::openDeleteSectionDialog,
        updateDb = ::updateAllSections,
        onPasswordClick = ::onLockIconClick
    )

    /**
     * Security dialog
     */
    private val securityDialog by lazy { SecurityDialog() }

    /** Crate / Edit section dialog */
    private val createEditSectionDialog by lazy { CreateEditSectionDialog() }

    /** Delete section dialog */
    private val deleteSectionDialog by lazy { DeleteSectionDialog() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SectionPreferencesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSectionsRecyclerView()

        setupSectionsRecyclerViewItemTouchListener()

        setupFabCircleButton()

        stateObserver()
    }

    override fun onStart() {
        super.onStart()
        setFragmentResultListener()
    }

    private fun setupSectionsRecyclerView() {
        binding.sectionPreferencesRecyclerView.apply {
            adapter = sectionPreferencesAdapter
            hasFixedSize()
            setItemViewCacheSize(ZERO)
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false,
            )
        }
    }

    private fun setupSectionsRecyclerViewItemTouchListener() {
        sectionPreferencesAdapter?.let { adapter ->
            ItemTouchHelper(
                SectionTouchHelperCallback(
                    sectionsAdapterList = adapter
                ),
            ).attachToRecyclerView(binding.sectionPreferencesRecyclerView)
        }
    }

    private fun stateObserver() {
        viewModel.sectionEvent.observe(viewLifecycleOwner) { event: SectionsEventContract ->
            when (event) {
                is SectionsEventContract.ShowAllSections ->
                    sectionPreferencesAdapter?.submitList(event.sectionEntities)

                is SectionsEventContract.CheckPassword -> {
                    checkPassword(sectionId = event.sectionId)
                }

                is SectionsEventContract.CheckPasswordDeletion -> {
                    checkPasswordForSectionDeletion(sectionId = event.sectionId)
                }
            }
        }
    }

    private fun updateAllSections(listSection: List<Section>) {
        viewModel.updateSections(listSection)
    }

    private fun onLockIconClick(section: Section) {
        if (section.hasPassword) {
            viewModel.checkPasswordForSectionById(section.id)
        } else {
            val dialog = securityDialog.newInstance(
                type = Security.Create(
                    place = Place.SectionPlace(sectionId = section.id),
                    purpose = SecurityPurpose.CREATE_SECTION_PASSWORD
                )
            )
            if (!dialog.isVisible) {
                dialog.show(childFragmentManager, SecurityDialog::class.simpleName)
            }
        }
    }

    private fun checkPassword(sectionId: Int) {
        val dialog = securityDialog.newInstance(
            type = Security.Check(
                place = Place.SectionPlace(sectionId = sectionId),
                purpose = SecurityPurpose.CHECK_SECTION_PASSWORD_DELETE
            )
        )
        if (!dialog.isVisible) {
            dialog.show(childFragmentManager, SecurityDialog::class.simpleName)
        }
    }

    private fun checkPasswordForSectionDeletion(sectionId: Int) {
        val dialog = securityDialog.newInstance(
            type = Security.Check(
                place = Place.SectionPlace(sectionId = sectionId),
                purpose = SecurityPurpose.CHECK_SECTION_DELETE
            )
        )
        if (!dialog.isVisible) {
            dialog.show(childFragmentManager, SecurityDialog::class.simpleName)
        }
    }

    private fun openCreateSectionDialog() {
        val dialog = createEditSectionDialog.newInstance()
        if (!dialog.isVisible) {
            dialog.show(
                childFragmentManager, CreateEditSectionDialog::class.simpleName
            )
        }
    }

    private fun openEditSectionDialog(section: Section) {
        val dialog = createEditSectionDialog.newInstance(section)
        if (!dialog.isVisible) {
            dialog.show(
                childFragmentManager, CreateEditSectionDialog::class.simpleName
            )
        }
    }

    private fun openDeleteSectionDialog(section: Section) {
        if (section.hasPassword) {
            viewModel.checkPasswordForSectionDeletionById(section)
        } else {
            val dialog = deleteSectionDialog.newInstance(section.id)
            if (!dialog.isVisible) {
                dialog.show(
                    childFragmentManager, CreateEditSectionDialog::class.simpleName
                )
            }
        }
    }

    private fun setupFabCircleButton() {
        binding.fab.setOnClickListener {
            openCreateSectionDialog()
        }
    }

    /**
     * Listener fot Security dialog
     * Creation of password for section is implemented by [SecurityDialog],
     * but password checking on fragment can be for create or delete section
     */
    private fun setFragmentResultListener() {
        setFragmentResultListener(SUCCESS_CHECK_PASSWORD_RESULT) { _, bundle ->
            bundle.getParcelable<Security>(BUNDLE_PASSWORD_RESULT_SECURITY_TYPE)?.let { security ->
                when (security) {
                    is Security.Check -> {

                        when (security.place) {
                            is Place.SectionPlace -> {

                                when(security.purpose) {
                                    SecurityPurpose.CHECK_SECTION_DELETE -> viewModel.deleteSection(security.place.sectionId)
                                    SecurityPurpose.CHECK_SECTION_PASSWORD_DELETE -> viewModel.deletePassword(security.place.sectionId)
                                    else -> { /** Check password only for section deletion or unlocking */ }
                                }
                            }
                            else -> { /** In this fragment check password only for: section */ }
                        }
                    }
                    else -> { /** Check password only */ }
                }
            }
        }
    }

    override fun onDestroyView() {
        sectionPreferencesAdapter = null
        binding.sectionPreferencesRecyclerView.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
