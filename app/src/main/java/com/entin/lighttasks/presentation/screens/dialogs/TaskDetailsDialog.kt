package com.entin.lighttasks.presentation.screens.dialogs

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.TaskDetailsDialogBinding
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.util.getIconTaskDrawable
import com.entin.lighttasks.presentation.util.isOrientationLandscape
import com.entin.lighttasks.presentation.util.toFormattedDateString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskDetailsDialog : DialogFragment() {

    private var _binding: TaskDetailsDialogBinding? = null
    private val binding get() = _binding!!
    private val args: SortTasksByIconDialogArgs by navArgs()

    override fun onStart() {
        super.onStart()
        dialog?.let { dialog ->
            dialog.window?.apply {
                attributes?.windowAnimations = R.style.NoPaddingDialogTheme
                setGravity(Gravity.CENTER)
            }
            setDialogWidth(if (isOrientationLandscape(context)) 0.92 else 0.72)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = TaskDetailsDialogBinding.inflate(inflater, container, false)

        val task: Task = args.task
        val icon = getIconTaskDrawable(task)

        with(binding) {
            dialogTaskDetailsOpenWebButton.apply {
                isVisible = task.attachedLink.isNotEmpty()
            }
            dialogTaskDetailsOpenWebButton.setOnClickListener {
                findNavController().navigate(
                    TaskDetailsDialogDirections.actionGlobalUrlWebView(task.attachedLink)
                )
            }
            dialogTaskDetailsIconToShow.setImageResource(icon)
            dialogTaskDetailsTitle.text = task.title
            dialogTaskDetailsMessage.text = task.message
            dialogTaskDetailsCreated.text = getCreatedAtDateString(task.createdAt)
            if (task.isTaskExpired) {
                dialogTaskDetailsExpired.text = getExpiredDateString(task.expireDateFirst, task.expireDateSecond)
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

    private fun getCreatedAtDateString(createdAt: Long): String = resources.getString(
        R.string.created_date_format,
        createdAt.toFormattedDateString(),
    )

    private fun setDialogWidth(width: Double) {
        val newWidth = (resources.displayMetrics.widthPixels * width).toInt()
        dialog?.window?.setLayout(newWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
