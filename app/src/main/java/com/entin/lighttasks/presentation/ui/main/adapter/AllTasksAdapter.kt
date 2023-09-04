package com.entin.lighttasks.presentation.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.ItemBinding
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.util.RADIO_EMPTY_ID
import com.entin.lighttasks.presentation.util.RADIO_FOOD_ID
import com.entin.lighttasks.presentation.util.RADIO_HOME_ID
import com.entin.lighttasks.presentation.util.RADIO_REST_ID
import com.entin.lighttasks.presentation.util.RADIO_WORK_ID

class AllTasksAdapter(
    private val listener: OnClickOnEmpty,
) : ListAdapter<Task, AllTasksAdapter.TaskViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: TaskViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val view: ItemBinding) : RecyclerView.ViewHolder(view.root) {
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
                taskTitle.text = task.title
                taskMessage.text = task.message
                taskFinished.isChecked = task.finished

                taskMessage.visibility = if (task.message.isNotBlank()) View.VISIBLE else View.GONE
                taskImportant.visibility = if (task.important) View.VISIBLE else View.GONE

                taskGroupIcon.setImageResource(
                    when (task.group) {
                        RADIO_EMPTY_ID -> R.drawable.ic_nothing
                        RADIO_WORK_ID -> R.drawable.ic_work
                        RADIO_REST_ID -> R.drawable.ic_rest
                        RADIO_FOOD_ID -> R.drawable.ic_food
                        RADIO_HOME_ID -> R.drawable.ic_home
                        else -> R.drawable.ic_nothing
                    },
                )
            }
        }
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        // Логика перемещения элементов в адаптере
    }

    fun onLeftSwipe(position: Int) {
        // Логика свайпа влево
    }

    fun onRightSwipe(position: Int) {
        // Логика свайпа вправо
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id && oldItem.position == newItem.position
    }
}
