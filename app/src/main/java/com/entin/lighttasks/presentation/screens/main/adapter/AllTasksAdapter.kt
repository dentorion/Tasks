package com.entin.lighttasks.presentation.screens.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.TaskItemBinding
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.util.ZERO_LONG
import com.entin.lighttasks.presentation.util.checkForEmptyTitle
import com.entin.lighttasks.presentation.util.convertDpToPixel
import com.entin.lighttasks.presentation.util.getIconTaskDrawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections
import java.util.Date

class AllTasksAdapter(
    private val listener: OnClickOnEmpty,
    private val navigateToDeleteDialog: (Task) -> Unit,
    private val navigateToSortDialog: (Task) -> Unit,
    private val openTaskDetailsDialog: (Task) -> Unit,
    private val updateDb: (List<Task>) -> Unit,
) : ListAdapter<Task, AllTasksAdapter.TaskViewHolder>(DiffCallback()), ItemTouchHelperAdapter {

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: TaskViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    inner class TaskViewHolder(
        private val view: TaskItemBinding
    ) : RecyclerView.ViewHolder(view.root) {
        init {
            view.apply {
                root.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onTaskClick(task)
                    }
                }
                taskFinished.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onFinishedTaskClick(task, taskFinished.isChecked)
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
                taskFinished.isChecked = task.isFinished
                taskImportant.visibility = if (task.isImportant) View.VISIBLE else View.GONE
                taskGroupIcon.setImageResource(getIconTaskDrawable(task))
                taskUrlTag.isVisible = task.attachedLink.isNotEmpty()
                taskPhotoTag.isVisible = task.attachedPhoto.isNotEmpty()
                taskVoiceTag.isVisible = task.attachedVoice.isNotEmpty()

                // Height of task
                val fullHeightPx = convertDpToPixel(96.toFloat(), root.context).toInt()

                taskAlarmBellNotification.isVisible = task.alarmId != ZERO_LONG

                if (task.isTaskExpired) {
                    /** RANGE */
                    if (task.isRange && !task.isEvent) {
                        // Now....FirstDate....SecondDate
                        if (Date().time < task.expireDateFirst) {
                            taskExpiredBackground.visibility = View.INVISIBLE
                            taskExpiredIndicator.visibility = View.INVISIBLE
                            taskAlarm.apply {
                                visibility = View.VISIBLE
                                setImageDrawable(resources.getDrawable(R.drawable.ic_alarm_rose_light))
                            }
                        }

                        // FirstDate....Now....SecondDate
                        if (Date().time >= task.expireDateFirst && Date().time <= task.expireDateSecond) {
                            taskExpiredBackground.visibility = View.VISIBLE
                            taskExpiredIndicator.apply {
                                visibility = View.VISIBLE
                                val fullLengthPeriod = task.expireDateSecond - task.expireDateFirst
                                val lengthPassed = Date().time - task.expireDateFirst
                                val progressPercentage = (lengthPassed / fullLengthPeriod.toFloat()) * 100
                                val height = (progressPercentage * fullHeightPx / 100).toInt()
                                layoutParams.height = height
                            }
                            // hide alarm icon
                            taskAlarm.visibility = View.GONE
                        }

                        // FirstDate....SecondDate....Now
                        if (Date().time > task.expireDateSecond) {
                            taskExpiredBackground.visibility = View.INVISIBLE
                            taskExpiredIndicator.visibility = View.INVISIBLE
                            taskAlarm.apply {
                                visibility = View.VISIBLE
                                setImageDrawable(resources.getDrawable(R.drawable.ic_alarm_black))
                            }
                        }
                    }
                    /** EVENT */
                    if (task.isEvent && !task.isRange) {
                        // FirstDate....Now
                        if (Date().time > task.expireDateFirst) {
                            taskExpiredBackground.visibility = View.INVISIBLE
                            taskExpiredIndicator.visibility = View.INVISIBLE
                            taskAlarm.apply {
                                visibility = View.VISIBLE
                                setImageDrawable(resources.getDrawable(R.drawable.ic_alarm_black))
                            }
                        }

                        // Now....FirstDate
                        if (Date().time < task.expireDateFirst) {
                            taskExpiredBackground.visibility = View.INVISIBLE
                            taskExpiredIndicator.visibility = View.INVISIBLE
                            taskAlarm.apply {
                                visibility = View.VISIBLE
                                setImageDrawable(resources.getDrawable(R.drawable.ic_alarm_rose_light))
                            }
                        }

                        // Now
                        if (Date().day == Date(task.expireDateFirst).day) {
                            taskExpiredBackground.visibility = View.INVISIBLE
                            taskExpiredIndicator.visibility = View.INVISIBLE
                            taskAlarm.apply {
                                visibility = View.VISIBLE
                                setImageDrawable(resources.getDrawable(R.drawable.ic_alarm_red))
                            }
                        }
                    }
                } else {
                    /** Without date (is_task_expired == false) */
                    taskExpiredBackground.visibility = View.GONE
                    taskExpiredIndicator.visibility = View.GONE
                    taskAlarm.visibility = View.GONE
                }
            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val newCurrentList = mutableListOf<Task>().apply { addAll(currentList) }

        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(newCurrentList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(newCurrentList, i, i - 1)
            }
        }
        submitList(newCurrentList)

        job?.cancel()
        job = scope.launch {
            delay(500L)
            newCurrentList.mapIndexed { index, task -> task.position = index }
            updateDb(newCurrentList)
        }

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val task = currentList[viewHolder.absoluteAdapterPosition]

        when (direction) {
            ItemTouchHelper.START -> {
                navigateToSortDialog(task)
                // To prevent empty place of task in recyclerview
                notifyItemChanged(viewHolder.absoluteAdapterPosition)
            }

            ItemTouchHelper.END -> {
                navigateToDeleteDialog(task)
                // To prevent empty place of task in recyclerview
                notifyItemChanged(viewHolder.absoluteAdapterPosition)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id && oldItem.position == newItem.position
    }
}
