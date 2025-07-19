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
import com.emman.android.medialarm.presentation.viewmodels.CalendarViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var _binding: FragmentHomeBinding
    private val _viewModel: CalendarViewModel by viewModels()
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarAdapter = CalendarAdapter(onDayClicked = { date ->
            _viewModel.onDaySelected(date)
        })

        _binding.recyclerViewCalendar.apply {
            adapter = calendarAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        _binding.ivCalendarNext.setOnClickListener {
            _viewModel.selectNextWeek()
        }
        _binding.ivCalendarPrevious.setOnClickListener {
            _viewModel.selectPreviousWeek()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            _viewModel.uiState.collect { uiState ->
                _binding.tvMonthDate.text = uiState.headerText
                calendarAdapter.submitList(uiState.days)
            }
        }

    }


}