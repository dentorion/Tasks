package com.entin.lighttasks.presentation.ui.preferencies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.entin.lighttasks.BuildConfig
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.FragmentPreferencesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreferencesFragment : Fragment(R.layout.fragment_preferences) {

    private var _binding: FragmentPreferencesBinding? = null
    private val binding get() = _binding!!

    private val versionCode: Int = BuildConfig.VERSION_CODE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPreferencesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            preferencesVersionApp.text = "App version: ${versionCode}"
        }

        setHasOptionsMenu(true)
    }
}
