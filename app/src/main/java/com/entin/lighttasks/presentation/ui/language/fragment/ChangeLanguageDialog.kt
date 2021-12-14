package com.entin.lighttasks.presentation.ui.language.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.entin.lighttasks.R
import com.entin.lighttasks.presentation.activity.Refreshable
import com.entin.lighttasks.presentation.util.*

class ChangeLanguageDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.language, container, false)

        rootView.findViewById<ImageButton>(R.id.english).setOnClickListener {
            LANGUAGES[LANGUAGE_ENGLISH]?.let { value -> choice(value) }
        }
        rootView.findViewById<ImageButton>(R.id.russian).setOnClickListener {
            LANGUAGES[LANGUAGE_RUSSIAN]?.let { value -> choice(value) }
        }
        rootView.findViewById<ImageButton>(R.id.polish).setOnClickListener {
            LANGUAGES[LANGUAGE_POLISH]?.let { value -> choice(value) }
        }

        return rootView
    }

    private fun choice(argument: Int) {
        requireContext().getSharedPreferences(
            LANGUAGES_KEY,
            Context.MODE_PRIVATE
        )[LANGUAGES_KEY] = argument

        dismiss()

        (activity as? Refreshable)?.refresh()
    }
}