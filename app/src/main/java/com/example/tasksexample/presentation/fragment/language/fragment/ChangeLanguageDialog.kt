package com.example.tasksexample.presentation.fragment.language.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.tasksexample.R
import com.example.tasksexample.presentation.activity.Refreshable
import com.example.tasksexample.presentation.util.AppConstants.LANGUAGES
import com.example.tasksexample.presentation.util.AppConstants.LANGUAGES_KEY
import com.example.tasksexample.presentation.util.set
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ChangeLanguageDialog : DialogFragment() {

    private val singleItems = LANGUAGES
    private var checkedItem = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.choose_language))
            .setNeutralButton(resources.getString(R.string.language_change_no), null)
            .setPositiveButton(resources.getString(R.string.language_change_yes)) { _, _ ->

                requireContext().getSharedPreferences(
                    LANGUAGES_KEY,
                    Context.MODE_PRIVATE
                )[LANGUAGES_KEY] = checkedItem

                (activity as? Refreshable)?.refresh()
            }
            .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                checkedItem = which
            }
            .show()
    }
}