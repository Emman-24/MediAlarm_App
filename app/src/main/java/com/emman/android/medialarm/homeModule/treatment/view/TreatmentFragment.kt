package com.emman.android.medialarm.homeModule.treatment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.emman.android.medialarm.data.local.DayItem
import com.emman.android.medialarm.databinding.FragmentTreatmentBinding
import com.emman.android.medialarm.homeModule.treatment.adapter.CalendarAdapter
import com.emman.android.medialarm.homeModule.treatment.adapter.TreatmentAdapter
import com.emman.android.medialarm.homeModule.treatment.viewModel.TreatmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.ZoneId

@AndroidEntryPoint
class TreatmentFragment : Fragment() {

    private lateinit var _binding: FragmentTreatmentBinding

    private val viewModel: TreatmentViewModel by viewModels()

    private lateinit var calendarAdapter: CalendarAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTreatmentBinding.inflate(inflater, container, false)
        _binding.lifecycleOwner = viewLifecycleOwner
        _binding.viewModel = viewModel
        _binding.adapter = TreatmentAdapter()
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalendarRecycler()
        calendarAdapter.apply {
            val days = generateDays()
            submitList(days)
        }

    }

    private fun setupCalendarRecycler() {
        calendarAdapter = CalendarAdapter(
            onDayClick = { dayItem -> viewModel.loadMedicinesForDayOrder(dayItem.date) }
        )

        _binding.calendarRecyclerView.apply {
            adapter = calendarAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

    }


    private fun generateDays(numberOfDays: Int = 31): List<DayItem> {
        val today = LocalDate.now(ZoneId.systemDefault())
        val days = (0 until numberOfDays).map {
            val date = today.plusDays(it.toLong())
            DayItem(
                date = date,
                time = date.atStartOfDay().toLocalTime(),
                isToday = date == today,
            )
        }
        return days
    }

}