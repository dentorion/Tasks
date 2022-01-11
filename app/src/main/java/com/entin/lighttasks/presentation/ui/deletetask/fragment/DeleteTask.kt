package com.entin.lighttasks.presentation.ui.deletetask.fragment

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
import com.entin.lighttasks.presentation.ui.main.viewmodel.AllTasksViewModel
import com.entin.lighttasks.presentation.ui.remote.viewmodel.RemoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DeleteTask : DialogFragment() {
    private val args: DeleteTaskArgs by navArgs()

    private val vmLocal: AllTasksViewModel by activityViewModels()
    private val vmRemote: RemoteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.delete_task, container, false)

        val task: Task? = args.task

        rootView.findViewById<TextView>(R.id.cancelBtn).setOnClickListener {
            dismiss()
        }

        rootView.findViewById<TextView>(R.id.okBtn).setOnClickListener {
            task?.let {
                if (args.remote) {
                    vmRemote.deleteRemoteTask(task)
                } else {
                    vmLocal.onTaskSwipedDelete(task)
                }
            }
            dismiss()
        }

        return rootView
    }

}