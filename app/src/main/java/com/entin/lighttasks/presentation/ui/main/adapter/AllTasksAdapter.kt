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

class AllTasksAdapter(
    private val listener: OnClickOnEmpty,
) : ListAdapter<Task, AllTasksAdapter.TaskViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: TaskViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
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
            binding.apply {
                taskTitle.text = task.title
                taskMessage.text = task.message
                taskFinished.isChecked = task.finished

                taskMessage.visibility = if (task.message.isNotBlank()) View.VISIBLE else View.GONE
                taskImportant.visibility = if (task.important) View.VISIBLE else View.GONE

                val taskImageChoice: Int = when (task.group) {
                    R.id.radio_empty -> R.drawable.ic_nothing
                    R.id.radio_rest -> R.drawable.ic_rest
                    R.id.radio_work -> R.drawable.ic_work
                    R.id.radio_home -> R.drawable.ic_home
                    R.id.radio_food -> R.drawable.ic_food
                    else -> R.drawable.ic_nothing
                }

                taskGroupIcon.setImageResource(taskImageChoice)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id && oldItem.position == newItem.position

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem
    }
}