package com.entin.lighttasks.presentation.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.entin.lighttasks.R
import com.entin.lighttasks.presentation.ui.main.AllTasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DeleteFinishedTasksDialog : DialogFragment() {

    private val vm: AllTasksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val rootView = inflater.inflate(R.layout.clear_finished_dialog, container, false)

        rootView.findViewById<TextView>(R.id.dialog_clear_finished_cancel_button).setOnClickListener {
            dismiss()
        }

        rootView.findViewById<TextView>(R.id.dialog_clear_finished_ok_button).setOnClickListener {
            vm.deleteFinishedTasks() {
                dismiss()
            }
        }

        return rootView
    }
}
