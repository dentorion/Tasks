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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections

class AllTasksAdapter(
    private val listener: OnClickOnEmpty,
    private val navigateToTaskScreen: (Task) -> Unit,
    private val updateDb: (List<Task>) -> Unit,
) : ListAdapter<Task, AllTasksAdapter.TaskViewHolder>(DiffCallback()), ItemTouchHelperAdapter {

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

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
                        0 -> R.drawable.ic_nothing
                        1 -> R.drawable.ic_work
                        2 -> R.drawable.ic_rest
                        3 -> R.drawable.ic_food
                        4 -> R.drawable.ic_home
                        5 -> R.drawable.ic_fish
                        6 -> R.drawable.ic_bird
                        7 -> R.drawable.ic_blueberries
                        8 -> R.drawable.ic_bicycle
                        9 -> R.drawable.ic_saw
                        10 -> R.drawable.ic_camera
                        11 -> R.drawable.ic_broom
                        12 -> R.drawable.ic_film
                        13 -> R.drawable.ic_collision
                        14 -> R.drawable.ic_coconut
                        15 -> R.drawable.ic_beer
                        16 -> R.drawable.ic_boy
                        17 -> R.drawable.ic_underwear
                        18 -> R.drawable.ic_balloon
                        19 -> R.drawable.ic_alien
                        20 -> R.drawable.ic_car
                        21 -> R.drawable.ic_amphora
                        22 -> R.drawable.ic_accordion
                        23 -> R.drawable.ic_airplane
                        24 -> R.drawable.ic_tree
                        25 -> R.drawable.ic_bandage
                        26 -> R.drawable.ic_deer
                        27 -> R.drawable.ic_knife
                        else -> R.drawable.ic_nothing
                    },
                )
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
        navigateToTaskScreen(task)

        // To prevent empty place of task in recyclerview
        notifyItemChanged(viewHolder.absoluteAdapterPosition)
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id && oldItem.position == newItem.position
    }
}
