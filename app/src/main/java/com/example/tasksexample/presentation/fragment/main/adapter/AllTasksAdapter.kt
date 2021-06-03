package com.example.tasksexample.presentation.fragment.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tasksexample.R
import com.example.tasksexample.databinding.ItemBinding
import com.example.tasksexample.domain.entity.Task

class AllTasksAdapter(private val listener: OnClickOnEmpty) :
    ListAdapter<Task, AllTasksAdapter.TaskViewHolder>(DiffCallback()) {

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

                val taskImageChoice: Int = when(task.group) {
                    R.id.radio_nothing -> {
                       R.drawable.ic_nothing
                    }
                    R.id.radio_rest -> {
                        R.drawable.ic_rest
                    }
                    R.id.radio_work -> {
                        R.drawable.ic_work
                    }
                    R.id.radio_home -> {
                        R.drawable.ic_home
                    }
                    R.id.radio_food -> {
                        R.drawable.ic_food
                    }
                    else -> R.drawable.ic_nothing
                }

                taskGroupIcon.setImageResource(taskImageChoice)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem
    }
}