package com.entin.lighttasks.presentation.ui.calendar.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.databinding.SimpleTaskItemBinding
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.util.checkForEmptyTitle
import com.entin.lighttasks.presentation.util.convertDpToPixel
import com.entin.lighttasks.presentation.util.getIconTaskDrawable
import java.util.Date

class SimpleAllTasksAdapter(
    private val openTaskEditScreen: (Task) -> Unit,
    private val openTaskDetailsDialog: (Task) -> Unit,
) : ListAdapter<Task, SimpleAllTasksAdapter.TaskViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = SimpleTaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: TaskViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val view: SimpleTaskItemBinding) :
        RecyclerView.ViewHolder(view.root) {
        init {
            view.apply {
                root.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        openTaskEditScreen(task)
                    }
                }
            }
        }

        fun bind(task: Task) {
            view.apply {
                root.setOnLongClickListener {
                    openTaskDetailsDialog(task)
                    true
                }

                taskTitle.text = checkForEmptyTitle(task.title, this.root.resources, task.id)
                taskMessage.apply {
                    visibility = if (task.message.isNotEmpty()) View.VISIBLE else View.GONE
                    text = task.message
                }
                taskImportant.visibility = if (task.isImportant) View.VISIBLE else View.GONE
                taskGroupIcon.setImageResource(getIconTaskDrawable(task))

                // Height of task
                val fullHeightPx = convertDpToPixel(96.toFloat(), root.context).toInt()

                /** Range */

                if (task.isTaskExpired) {

                    if (task.isRange) {

                        // Now....FirstDate....SecondDate
                        if (Date().time < task.expireDateFirst) {
                            taskExpiredBackground.visibility = View.INVISIBLE
                            taskExpiredIndicator.visibility = View.INVISIBLE
                        }

                        // FirstDate....Now....SecondDate
                        if (Date().time >= task.expireDateFirst && Date().time <= task.expireDateSecond) {
                            taskExpiredBackground.visibility = View.VISIBLE
                            taskExpiredIndicator.apply {
                                visibility = View.VISIBLE
                                val fullLengthPeriod = task.expireDateSecond - task.expireDateFirst
                                val lengthPassed = Date().time - task.expireDateFirst
                                val progressPercentage =
                                    (lengthPassed / fullLengthPeriod.toFloat()) * 100
                                val height = (progressPercentage * fullHeightPx / 100).toInt()
                                layoutParams.height = height
                            }
                        }

                        // FirstDate....SecondDate....Now
                        if (Date().time > task.expireDateSecond) {
                            taskExpiredBackground.visibility = View.INVISIBLE
                            taskExpiredIndicator.visibility = View.INVISIBLE
                        }
                    }

                    if (task.isEvent) {

                        // FirstDate....Now
                        if (Date().time > task.expireDateFirst) {
                            Log.e("STATUS", "FirstDate....Now")
                            taskExpiredBackground.visibility = View.INVISIBLE
                            taskExpiredIndicator.visibility = View.INVISIBLE
                        }

                        // Now....FirstDate
                        if (Date().time < task.expireDateFirst) {
                            Log.e("STATUS", "Now....FirstDate")
                            taskExpiredBackground.visibility = View.INVISIBLE
                            taskExpiredIndicator.visibility = View.INVISIBLE
                        }

                        // Now
                        if (Date().day == Date(task.expireDateFirst).day) {
                            Log.e(
                                "STATUS",
                                "Now: day:${Date().day} == ${Date(task.expireDateFirst).day}"
                            )
                            taskExpiredBackground.visibility = View.INVISIBLE
                            taskExpiredIndicator.visibility = View.INVISIBLE
                        }
                    }
                } else {
                    /** Event */

                    // Without date
                    taskExpiredBackground.visibility = View.GONE
                    taskExpiredIndicator.visibility = View.GONE
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id && oldItem.position == newItem.position
    }
}
