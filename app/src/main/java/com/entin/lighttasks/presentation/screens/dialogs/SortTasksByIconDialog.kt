package com.entin.lighttasks.presentation.screens.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.entin.lighttasks.R
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.screens.main.AllTasksViewModel
import com.entin.lighttasks.presentation.util.getIconTaskDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SortTasksByIconDialog : DialogFragment() {
    private val args: SortTasksByIconDialogArgs by navArgs()
    private val vmLocal: AllTasksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val rootView = inflater.inflate(R.layout.sort_task_dialog, container, false)

        val task: Task = args.task

        val icon = getIconTaskDrawable(task)
        rootView.findViewById<ImageView>(R.id.dialog_sort_task_icon_to_show).setImageResource(icon)

        rootView.findViewById<TextView>(R.id.dialog_sort_task_cancel_button).setOnClickListener {
            dismiss()
        }

        rootView.findViewById<TextView>(R.id.dialog_sort_task_ok_button).setOnClickListener {
            task.let {
                vmLocal.onTaskSortByIcon(task)
            }
            dismiss()
        }

        return rootView
    }
}
