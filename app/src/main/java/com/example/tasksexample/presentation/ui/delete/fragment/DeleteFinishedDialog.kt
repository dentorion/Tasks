package com.example.tasksexample.presentation.ui.delete.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.tasksexample.R
import com.example.tasksexample.presentation.ui.main.viewmodel.AllTasksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteFinishedDialog : DialogFragment() {

    private val vm: AllTasksViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.delete_sure))
            .setMessage(resources.getString(R.string.delete_message))
            .setNegativeButton(resources.getString(R.string.delete_no), null)
            .setPositiveButton(resources.getString(R.string.delete_yes)) { _, _ ->
                vm.deleteFinishedTasks()
            }
            .create()
    }
}