package com.entin.lighttasks.presentation.screens.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.FragmentCalendarBinding
import com.entin.lighttasks.domain.entity.DayItem
import com.entin.lighttasks.domain.entity.IconTask
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.screens.addedit.adapter.SlowlyLinearLayoutManager
import com.entin.lighttasks.presentation.screens.calendar.adapter.DayTasksAdapter
import com.entin.lighttasks.presentation.screens.calendar.adapter.DaysLineCalendarAdapter
import com.entin.lighttasks.presentation.screens.calendar.adapter.SortByIconAdapter
import com.entin.lighttasks.presentation.screens.main.AllTasksFragmentDirections
import com.entin.lighttasks.presentation.util.ONE
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.getMonthName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment for view as Calendar
 */

@AndroidEntryPoint
class CalendarFragment : Fragment(R.layout.fragment_calendar) {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CalendarViewModel by viewModels()
    private var iconListAdapter: SortByIconAdapter? = null
    private var taskListAdapter: DayTasksAdapter? = null
    private var daysListAdapter: DaysLineCalendarAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)

        setupIconGroupRecyclerView()
        setupCalendarListRecyclerView()
        setupTaskListRecyclerView()
        setupDaysCalendarObserver()
        setupIconGroupObserver()
        setupTodayButton()
        initStrictMonth()
        setupMonthSelector()

        return binding.root
    }

    /** Group icon */
    private fun setupIconGroupRecyclerView() {
        iconListAdapter = SortByIconAdapter(
            getSelectedIcon = ::getSelectedIcon
        ) { element, position ->
            onGroupIconSelected(element, position)
        }

        binding.calendarIconsRecyclerview.apply {
            adapter = iconListAdapter
            layoutManager = SlowlyLinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false,
            )
        }
    }

    private fun getSelectedIcon(): Int =
        viewModel.sortIcon ?: -ONE

    /** Calendar - List */
    private fun setupCalendarListRecyclerView() {
        daysListAdapter = DaysLineCalendarAdapter { element, position ->
            onDayCalendarSelected(element, position)
        }

        binding.calendarDaysRecyclerview.apply {
            adapter = daysListAdapter
            layoutManager = SlowlyLinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false,
            )
        }
    }

    /** Tasks - List */
    private fun setupTaskListRecyclerView() {
        taskListAdapter = DayTasksAdapter(
            openTaskEditScreen = ::openTaskEditScreen,
            openTaskDetailsDialog = ::openTaskDetailsDialog,
        )

        binding.calendarTasksRecyclerview.apply {
            adapter = taskListAdapter
            layoutManager = SlowlyLinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false,
            )
        }
    }

    private fun onGroupIconSelected(element: IconTask?, position: Int?) {
        position?.let {
            if(element == null) {
                viewModel.sortIcon = null
                binding.calendarIconsRecyclerview.smoothScrollToPosition(it)
            } else {
                viewModel.sortIcon = element.groupId
                binding.calendarIconsRecyclerview.smoothScrollToPosition(it)
            }
        }
    }

    private fun onDayCalendarSelected(element: DayItem?, position: Int?) {
        position?.let { positionInRv ->
            element?.let { element ->
                setTasks(element.listOfTasks)
                binding.calendarDaysRecyclerview.smoothScrollToPosition(positionInRv)
            }
        }
    }

    /** Icon Group observer */
    private fun setupDaysCalendarObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                /** Days */
                viewModel.calendarChannel.collect { event: CalendarEventContract ->
                    when (event) {
                        is CalendarEventContract.UpdateCalendarAndMonth -> {
                            setupDaysCalendar(event.listOfDays)
                            setupMonthName(event.monthSequenceNumber)
                        }
                    }
                }
            }
        }
    }

    /** Icon group observer */
    private fun setupIconGroupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                /** Icons */
                viewModel.iconGroupsChannel.collect { icons ->
                    iconListAdapter?.submitList(icons)
                }
            }
        }
    }

    /** Go to today in calendar */
    private fun setupTodayButton() {
        binding.calendarDaysTodayButton.setOnClickListener {
            daysListAdapter?.currentList?.firstOrNull { it.isToday }?.let { today ->
                setTasks(today.listOfTasks)
                daysListAdapter?.selectedItem = today
                val indexOfSelectedIcon = daysListAdapter?.currentList?.indexOfFirst { it == today } ?: ZERO
                daysListAdapter?.notifyDataSetChanged()
                binding.calendarDaysRecyclerview.scrollToPosition(indexOfSelectedIcon)
            } ?: kotlin.run {
                viewModel.setTodayFromOtherMonth()
            }
        }
    }

    /** Strict month */
    private fun initStrictMonth() {
        binding.calendarFinishDateWithinMonthCheckbox.apply {
            this.isChecked = viewModel.strictMonth
            this.jumpDrawablesToCurrentState()
        }
        binding.calendarFinishDateWithinMonthCheckbox.setOnCheckedChangeListener { _, isCheck ->
            viewModel.strictMonth = isCheck
        }
    }

    /** Calendar month selection */
    private fun setupMonthSelector() {
        binding.apply {
            calendarLeftMonth.setOnClickListener {
                viewModel.setMonthCalendar(false)
            }
            calendarRightMonth.setOnClickListener {
                viewModel.setMonthCalendar(true)
            }
        }
    }

    /** Set days in calendar */
    private fun setupDaysCalendar(listOfDays: List<DayItem>) {
        daysListAdapter?.let { adapter ->
            val selectedDay =
                if (adapter.selectedItem == null) {
                    // First time
                    listOfDays.firstOrNull { it.isToday } ?: listOfDays.first()
                } else {
                    // Next time
                    listOfDays.firstOrNull {
                        it.dayNumber == (adapter.selectedItem?.dayNumber ?: ONE)
                    } ?: listOfDays.first()
                }

            adapter.apply {
                selectedItem = selectedDay
                adapter.submitList(listOfDays)
            }
            val indexOfSelectedDay = listOfDays.indexOf(selectedDay)
            binding.calendarDaysRecyclerview.scrollToPosition(indexOfSelectedDay)
            setTasks(selectedDay.listOfTasks)
        }
    }

    private fun setupMonthName(monthSequenceNumber: Int) {
        binding.calendarMonthLabel.text = resources.getString(getMonthName(monthSequenceNumber))
    }

    private fun setTasks(listOfTasks: List<Task>) {
        taskListAdapter?.submitList(listOfTasks)
    }

    private fun openTaskEditScreen(task: Task) {
        openTaskDetailsDialog(task)
    }

    private fun openTaskDetailsDialog(task: Task) {
        findNavController().navigate(AllTasksFragmentDirections.actionGlobalTaskDetailsDialog(task))
    }

    override fun onDestroyView() {
        iconListAdapter = null
        _binding = null
        super.onDestroyView()
    }
}
