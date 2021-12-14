package com.entin.lighttasks.presentation.ui.deleteall.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.entin.lighttasks.R
import com.entin.lighttasks.presentation.ui.main.viewmodel.AllTasksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteFinishedDialog : DialogFragment() {

    private val vm: AllTasksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.clear_finished, container, false)

        rootView.findViewById<TextView>(R.id.cancelBtn).setOnClickListener {
            dismiss()
        }

        rootView.findViewById<TextView>(R.id.okBtn).setOnClickListener {
            vm.deleteFinishedTasks() {
                dismiss()
            }
        }

        return rootView
    }

}