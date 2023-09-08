package com.entin.lighttasks.presentation.ui.dialogs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.entin.lighttasks.R
import com.entin.lighttasks.presentation.activity.MainActivity
import com.entin.lighttasks.presentation.util.LANGUAGE_ENGLISH_SHOT
import com.entin.lighttasks.presentation.util.LANGUAGE_POLISH_SHOT
import com.entin.lighttasks.presentation.util.LANGUAGE_SPANISH_SHOT
import com.entin.lighttasks.presentation.util.LANGUAGE_UKRAINE_SHOT
import java.util.Locale

class ChangeLanguageDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val rootView = inflater.inflate(R.layout.change_language_dialog, container, false)

        rootView.findViewById<ImageButton>(R.id.dialog_change_language_english).setOnClickListener {
            setAppLocale(requireContext(), LANGUAGE_ENGLISH_SHOT)
        }
        rootView.findViewById<ImageButton>(R.id.dialog_change_language_spanish).setOnClickListener {
            setAppLocale(requireContext(), LANGUAGE_SPANISH_SHOT)
        }
        rootView.findViewById<ImageButton>(R.id.dialog_change_language_polish).setOnClickListener {
            setAppLocale(requireContext(), LANGUAGE_POLISH_SHOT)
        }
        rootView.findViewById<ImageButton>(R.id.dialog_change_language_ukraine).setOnClickListener {
            setAppLocale(requireContext(), LANGUAGE_UKRAINE_SHOT)
        }

        return rootView
    }

    // Language changing
    @Suppress("DEPRECATION")
    private fun setAppLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        dismiss()
        Intent(requireContext(), MainActivity::class.java).also {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(it)
        }
    }
}