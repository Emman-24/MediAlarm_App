package com.emman.android.medialarm.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.emman.android.medialarm.databinding.FragmentHomeBinding
import com.emman.android.medialarm.presentation.adapter.CalendarAdapter
import com.emman.android.medialarm.presentation.adapter.MedicineScheduleAdapter
import com.emman.android.medialarm.presentation.viewmodels.CalendarViewModel
import com.emman.android.medialarm.presentation.viewmodels.MedicineViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var _binding: FragmentHomeBinding
    private val _calendarViewModel: CalendarViewModel by viewModels()
    private val _medicineViewModel: MedicineViewModel by viewModels()
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var medicineScheduleAdapter: MedicineScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = LocalDate.now()
        _calendarViewModel.initCalendar(currentDate)
        _medicineViewModel.getMedicineSchedules(currentDate)

        setupRecyclerView()

        setupClickListeners()

        observeViewModels()


    }

    private fun setupRecyclerView() {
        calendarAdapter = CalendarAdapter(onDayClicked = { date ->
            _calendarViewModel.onDaySelected(date)
            updateDayTitle(date)
            _medicineViewModel.getMedicineSchedules(date)
        })
        _binding.recyclerViewCalendar.apply {
            adapter = calendarAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        medicineScheduleAdapter = MedicineScheduleAdapter()
        _binding.medicineSchedule.apply {
            adapter = medicineScheduleAdapter
            layoutManager = LinearLayoutManager(context)
        }


    }

    private fun setupClickListeners() {
        _binding.ivCalendarNext.setOnClickListener {
            _calendarViewModel.selectNextWeek()
        }
        _binding.ivCalendarPrevious.setOnClickListener {
            _calendarViewModel.selectPreviousWeek()
        }
    }


    private fun observeViewModels() {
        viewLifecycleOwner.lifecycleScope.launch {
            _calendarViewModel.uiState.collect { uiState ->
                _binding.tvMonthDate.text = uiState.headerText
                calendarAdapter.submitList(uiState.days)
                updateDayTitle(uiState.selectedDate)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            _medicineViewModel.uiState.collect { uiState ->
                medicineScheduleAdapter.submitList(uiState.schedules)
                if (uiState.isLoading) {
                    // Show loading indicator
                } else if (uiState.error != null) {
                    // Show error message
                }
            }
        }
    }

    private fun updateDayTitle(date: LocalDate) {
        val today = LocalDate.now()
        val isToday = date.isEqual(today)

        _binding.tvDay.text = if (isToday) {
            "Today's Medications"
        } else {
            val dayName = date.format(DateTimeFormatter.ofPattern("EEEE"))
            "$dayName's Medications"
        }
    }
}
