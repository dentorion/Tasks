package com.entin.lighttasks.presentation.ui.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.TaskItemBinding
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.util.convertDpToPixel
import com.entin.lighttasks.presentation.util.convertPixelsToDp
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

    inner class TaskViewHolder(private val view: TaskItemBinding) :
        RecyclerView.ViewHolder(view.root) {
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
//                    openTaskDetailsDialog(task)
                    true
                }

                taskTitle.text = task.title
                taskMessage.apply {
                    visibility = if (task.message.isNotEmpty()) View.VISIBLE else View.GONE
                    text = task.message
                }
                taskFinished.isChecked = task.finished
                taskImportant.visibility = if (task.important) View.VISIBLE else View.GONE
                taskGroupIcon.setImageResource(getIconTaskDrawable(task))

                // Height of task
                val fullHeightPx = convertDpToPixel(96.toFloat(), root.context).toInt()

                // FirstDate....Now....SecondDate
                if (task.isTaskExpired && task.isRange && Date().time >= task.expireDateFirst && Date().time <= task.expireDateSecond) {
                    taskExpiredBackground.visibility = View.VISIBLE
                    taskExpiredIndicator.apply {
                        visibility = View.VISIBLE
                        val fullLengthPeriod = task.expireDateSecond - task.expireDateFirst
                        val lengthPassed = Date().time - task.expireDateFirst
                        val progressPercentage = (lengthPassed / fullLengthPeriod.toFloat()) * 100
                        val height = (progressPercentage * fullHeightPx / 100).toInt()

                        Log.e(
                            "EBLAN",
                            "..NOW.. fullHeightPx: $fullHeightPx, fullLengthPeriod: $fullLengthPeriod, lengthPassed: $lengthPassed, progressPercentage: $progressPercentage, height: $height px / ${
                                convertPixelsToDp(
                                    height,
                                    this.context
                                )
                            } dp"
                        )
                        layoutParams.height = height
                    }
                }

                // FirstDate....SecondDate....Now
                if (task.isTaskExpired && task.isRange && Date().time >= task.expireDateSecond) {
                    taskExpiredBackground.apply {
                        visibility = View.VISIBLE
                        setBackgroundColor(resources.getColor(R.color.color_main_light_extra))
                    }
                    taskExpiredIndicator.apply {
                        visibility = View.VISIBLE
                        setBackgroundColor(resources.getColor(R.color.task_expire_background_indicator))
                        layoutParams.height = convertDpToPixel(96.toFloat(), this.context).toInt()
                    }
                }

                // Without date
                if (!task.isTaskExpired || Date().time < task.expireDateFirst) {
                    taskExpiredBackground.visibility = View.GONE
                    taskExpiredIndicator.visibility = View.GONE
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
