package com.entin.lighttasks.presentation.ui.calendar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.DayItemBinding
import com.entin.lighttasks.domain.entity.DayItem
import java.time.DayOfWeek

class DaysLineCalendarAdapter(
    private val onClick: (element: DayItem?, position: Int?) -> Unit,
) : ListAdapter<DayItem, DaysLineCalendarAdapter.DayViewHolder>(DayDiffCallback) {

    var selectedItem: DayItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder =
        DayViewHolder(
            DayItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val dayItem = getItem(position)
        holder.bind(dayItem, position)
    }

    inner class DayViewHolder(
        private val binding: DayItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(dayItem: DayItem, position: Int) {
            binding.apply {
                dayItemNumberOfDay.setOnClickListener {
                    selectItem(dayItem, position)
                }

                when {
                    /** SELECTED DAY */
                    dayItem == selectedItem -> {

                        /** SELECTED DAY + DAY is TODAY */
                        if(dayItem.isToday) {
                            dayItemNumberOfDay.apply {
                                background = ResourcesCompat.getDrawable(
                                    this.context.resources,
                                    R.drawable.ic_circle_day_selected_today,
                                    context.theme,
                                )
                                setTextColor(resources.getColor(R.color.white))
                            }
                        }

                        /** SELECTED DAY + DAY is NOT TODAY */
                        else {
                            dayItemNumberOfDay.apply {
                                background = ResourcesCompat.getDrawable(
                                    this.context.resources,
                                    R.drawable.ic_circle_day_selected,
                                    context.theme,
                                )
                                setTextColor(resources.getColor(R.color.color_main))
                            }
                        }

                        /** SELECTED DAY for COUNT TASK */
                        dayItemCountTasks.apply {
                            background = ResourcesCompat.getDrawable(
                                this.context.resources,
                                R.drawable.ic_circle_calendar_count_tasks_selected,
                                context.theme,
                            )
                            setTextColor(resources.getColor(R.color.color_main))
                        }
                        dayItemCircleThatDay.visibility = View.VISIBLE
                    }

                    /** UNSELECTED DAY */
                    dayItem != selectedItem -> {

                        /** UNSELECTED DAY + SATURDAY or SUNDAY */
                        when(dayItem.dayOfWeek) {
                            DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> {

                                /** UNSELECTED DAY + SATURDAY or SUNDAY + DAY is TODAY */
                                if(dayItem.isToday) {
                                    dayItemNumberOfDay.apply {
                                        background = ResourcesCompat.getDrawable(
                                            this.context.resources,
                                            R.drawable.ic_circle_day_today_weekend,
                                            context.theme,
                                        )
                                        setTextColor(resources.getColor(R.color.white))
                                    }
                                }

                                /** UNSELECTED DAY + SATURDAY or SUNDAY + DAY is NOT TODAY */
                                else {
                                    dayItemNumberOfDay.apply {
                                        background = ResourcesCompat.getDrawable(
                                            this.context.resources,
                                            R.drawable.ic_circle_day_weekend,
                                            context.theme,
                                        )
                                        setTextColor(resources.getColor(R.color.color_main_light_extra))
                                    }
                                }
                            }

                            /** MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY */
                            else -> {

                                /** UNSELECTED DAY + MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY + DAY is TODAY */
                                if(dayItem.isToday) {
                                    dayItemNumberOfDay.apply {
                                        background = ResourcesCompat.getDrawable(
                                            this.context.resources,
                                            R.drawable.ic_circle_day_today,
                                            context.theme,
                                        )
                                        setTextColor(resources.getColor(R.color.white))
                                    }
                                }

                                /** UNSELECTED DAY + MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY + DAY is NOT TODAY */
                                else {
                                    dayItemNumberOfDay.apply {
                                        background = ResourcesCompat.getDrawable(
                                            this.context.resources,
                                            R.drawable.ic_circle_day_unselected,
                                            context.theme,
                                        )
                                        setTextColor(resources.getColor(R.color.task_expire_background_indicator))
                                    }
                                }
                            }
                        }

                        /** UNSELECTED DAY for COUNT TASK */
                        dayItemCountTasks.apply {
                            background = ResourcesCompat.getDrawable(
                                this.context.resources,
                                R.drawable.ic_circle_calendar_count_tasks_unselected,
                                context.theme,
                            )
                            setTextColor(resources.getColor(R.color.task_expire_background_indicator))
                        }
                        dayItemCircleThatDay.visibility = View.INVISIBLE
                    }
                }

                // Number of day
                dayItemNumberOfDay.text = dayItem.dayNumber.toString()
                // Day title
                dayItemDayTitle.apply {
                val resources = this.context.resources
                    text = when(dayItem.dayOfWeek) {
                        DayOfWeek.MONDAY -> { resources.getString(R.string.sort_calendar_monday_short) }
                        DayOfWeek.TUESDAY -> { resources.getString(R.string.sort_calendar_tuesday_short) }
                        DayOfWeek.WEDNESDAY -> { resources.getString(R.string.sort_calendar_wednesday_short) }
                        DayOfWeek.THURSDAY -> { resources.getString(R.string.sort_calendar_thursday_short) }
                        DayOfWeek.FRIDAY -> { resources.getString(R.string.sort_calendar_friday_short) }
                        DayOfWeek.SATURDAY -> { resources.getString(R.string.sort_calendar_saturday_short) }
                        DayOfWeek.SUNDAY -> { resources.getString(R.string.sort_calendar_sunday_short) }
                    }
                    setTextColor(
                        when(dayItem.dayOfWeek) {
                            DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> {
                                resources.getColor(R.color.color_important)
                            }
                            else -> {
                                resources.getColor(R.color.gray)
                            }
                        }
                    )
                }

                // Count of tasks
                if (dayItem.listOfTasks.isNotEmpty()) {
                    dayItemCountTasks.text = dayItem.listOfTasks.count().toString()
                    dayItemCountTasks.visibility = View.VISIBLE
                } else {
                    dayItemCountTasks.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun selectItem(value: DayItem, position: Int?) {
        when {
            value == selectedItem -> {
//                onClick(null, position)
//                selectedItem = null
            }

            value != selectedItem -> {
                onClick(value, position)
                selectedItem = value
            }
        }
        notifyDataSetChanged()
    }

    companion object {
        private val DayDiffCallback =
            object : DiffUtil.ItemCallback<DayItem>() {
                override fun areItemsTheSame(oldItem: DayItem, newItem: DayItem): Boolean {
                    return oldItem.dayNumber == newItem.dayNumber &&
                            oldItem.isToday == newItem.isToday &&
                            oldItem.listOfTasks.count() == newItem.listOfTasks.count()
                }

                override fun areContentsTheSame(oldItem: DayItem, newItem: DayItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
