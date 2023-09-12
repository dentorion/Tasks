package com.entin.lighttasks.presentation.ui.dialogs

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
import com.entin.lighttasks.presentation.ui.main.AllTasksViewModel
import com.entin.lighttasks.presentation.util.getIconTaskDrawable
import com.entin.lighttasks.presentation.util.toFormattedDateString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskDetailsDialog : DialogFragment() {
    private val args: SortTasksByIconDialogArgs by navArgs()
    private val viewModel: AllTasksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val rootView = inflater.inflate(R.layout.task_details_dialog, container, false)

        val task: Task = args.task
        val icon = getIconTaskDrawable(task)
        val iconToShow = rootView.findViewById<ImageView>(R.id.dialog_task_details_icon_to_show)
        val title = rootView.findViewById<TextView>(R.id.dialog_task_details_title)
        val message = rootView.findViewById<TextView>(R.id.dialog_task_details_message)
        val close = rootView.findViewById<TextView>(R.id.dialog_task_details_cancel_button)
        val created = rootView.findViewById<TextView>(R.id.dialog_task_details_created)
        val expired = rootView.findViewById<TextView>(R.id.dialog_task_details_expired)

        iconToShow.setImageResource(icon)
        title.text = task.title
        message.text = task.message
        created.text = task.createdAt.toFormattedDateString()
        if (task.isTaskExpired) {
            expired.text = getExpiredDateString(task.expireDateFirst, task.expireDateSecond)
        } else {
            expired.visibility = View.INVISIBLE
        }
        close.setOnClickListener { dismiss() }

        return rootView
    }

    private fun getExpiredDateString(dateFirst: Long, dateSecond: Long): String =
        resources.getString(
            R.string.expiration_date_format,
            dateFirst.toFormattedDateString(),
            dateSecond.toFormattedDateString()
        )
}
