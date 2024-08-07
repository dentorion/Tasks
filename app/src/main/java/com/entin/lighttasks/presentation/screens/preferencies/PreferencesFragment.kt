package com.entin.lighttasks.presentation.screens.preferencies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.entin.lighttasks.BuildConfig
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.FragmentPreferencesBinding
import com.entin.lighttasks.presentation.screens.main.AllTasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class PreferencesFragment : Fragment(R.layout.fragment_preferences) {

    private var _binding: FragmentPreferencesBinding? = null
    private val binding get() = _binding!!

    private val versionCode: Int = BuildConfig.VERSION_CODE

    private val viewModel: PreferencesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPreferencesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            preferencesVersionApp.text = "App version: ${versionCode}"
        }

//        viewModel.getTestHttpResponse()

//        viewModel.launchTest()
        viewModel.asyncTest()

        setHasOptionsMenu(true)
    }
}
