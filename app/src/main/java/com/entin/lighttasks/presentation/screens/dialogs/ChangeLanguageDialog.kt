package com.entin.lighttasks.presentation.screens.dialogs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.entin.lighttasks.databinding.ChangeLanguageDialogBinding
import com.entin.lighttasks.presentation.activity.MainActivity
import com.entin.lighttasks.presentation.util.LANGUAGE
import com.entin.lighttasks.presentation.util.LANGUAGE_ENGLISH_SHOT
import com.entin.lighttasks.presentation.util.LANGUAGE_POLISH_SHOT
import com.entin.lighttasks.presentation.util.LANGUAGE_SPANISH_SHOT
import com.entin.lighttasks.presentation.util.LANGUAGE_UKRAINE_SHOT
import com.entin.lighttasks.presentation.util.set
import java.util.Locale

class ChangeLanguageDialog : DialogFragment() {

    private var _binding: ChangeLanguageDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        isCancelable = false
        _binding = ChangeLanguageDialogBinding.inflate(inflater, container, false)

        with(binding) {
            dialogChangeLanguageEnglish.setOnClickListener {
                setAppLocale(requireContext(), LANGUAGE_ENGLISH_SHOT)
            }

            dialogChangeLanguageSpanish.setOnClickListener {
                setAppLocale(requireContext(), LANGUAGE_SPANISH_SHOT)
            }

            dialogChangeLanguagePolish.setOnClickListener {
                setAppLocale(requireContext(), LANGUAGE_POLISH_SHOT)
            }

            dialogChangeLanguageUkraine.setOnClickListener {
                setAppLocale(requireContext(), LANGUAGE_UKRAINE_SHOT)
            }
        }

        return binding.root
    }

    // Language changing
    @Suppress("DEPRECATION")
    private fun setAppLocale(context: Context, language: String) {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        sharedPref[LANGUAGE] = language

        dismiss()
        Intent(requireContext(), MainActivity::class.java).also {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(it)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
