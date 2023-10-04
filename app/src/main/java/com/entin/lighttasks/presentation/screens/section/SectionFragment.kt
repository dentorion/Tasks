package com.entin.lighttasks.presentation.screens.section

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.SectionPreferencesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class SectionFragment : Fragment(R.layout.all_tasks) {

    private var _binding: SectionPreferencesBinding? = null
    private val binding get() = _binding!!

    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModel: SectionViewModel by activityViewModels()
}
