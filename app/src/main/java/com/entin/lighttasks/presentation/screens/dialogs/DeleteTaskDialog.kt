package com.entin.lighttasks.presentation.screens.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.entin.lighttasks.R
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.screens.main.AllTasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DeleteTaskDialog : DialogFragment() {
    private val args: DeleteTaskDialogArgs by navArgs()
    private val vmLocal: AllTasksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val rootView = inflater.inflate(R.layout.delete_task_dialog, container, false)

        val task: Task = args.task

        rootView.findViewById<TextView>(R.id.dialog_delete_task_cancel_button).setOnClickListener {
            dismiss()
        }

        rootView.findViewById<TextView>(R.id.dialog_delete_task_ok_button).setOnClickListener {
            task.let {
                vmLocal.onTaskSwipedDelete(task)
            }
            dismiss()
        }

        return rootView
    }
}
