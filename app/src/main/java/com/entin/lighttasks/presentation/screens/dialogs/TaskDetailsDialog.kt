package com.entin.lighttasks.presentation.screens.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.TaskDetailsDialogBinding
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.util.getIconTaskDrawable
import com.entin.lighttasks.presentation.util.toFormattedDateString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskDetailsDialog : DialogFragment() {

    private var _binding: TaskDetailsDialogBinding? = null
    private val binding get() = _binding!!
    private val args: SortTasksByIconDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = TaskDetailsDialogBinding.inflate(inflater, container, false)

        val task: Task = args.task
        val icon = getIconTaskDrawable(task)

        with(binding) {
            dialogTaskDetailsIconToShow.setImageResource(icon)
            dialogTaskDetailsTitle.text = task.title
            dialogTaskDetailsMessage.text = task.message
            dialogTaskDetailsCreated.text = task.createdAt.toFormattedDateString()
            if (task.isTaskExpired) {
                dialogTaskDetailsExpired.text =
                    getExpiredDateString(task.expireDateFirst, task.expireDateSecond)
            } else {
                dialogTaskDetailsExpired.visibility = View.INVISIBLE
            }
            dialogTaskDetailsCancelButton.setOnClickListener { dismiss() }
        }

        return binding.root
    }

    private fun getExpiredDateString(dateFirst: Long, dateSecond: Long): String =
        resources.getString(
            R.string.expiration_date_format,
            dateFirst.toFormattedDateString(),
            dateSecond.toFormattedDateString()
        )

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
