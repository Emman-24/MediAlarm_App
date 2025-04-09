package com.emman.android.medialarm.createModule.schedule

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.emman.android.medialarm.R
import com.emman.android.medialarm.createModule.CreateViewModel
import com.emman.android.medialarm.databinding.FragmentScheduleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private val _viewModel: CreateViewModel by viewModels()
    private lateinit var _binding: FragmentScheduleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.expandableContentMultipleTimes.visibility =
            if (_binding.materialSwitchMultipleTimes.isChecked) View.VISIBLE else View.GONE
        _binding.expandableContentSpecificDays.visibility =
            if (_binding.materialSwitchSpecificDays.isChecked) View.VISIBLE else View.GONE
        _binding.expandableContentCyclic.visibility =
            if (_binding.materialSwitchCyclic.isChecked) View.VISIBLE else View.GONE
        _binding.expandableContentInterval.visibility =
            if (_binding.materialSwitchInterval.isChecked) View.VISIBLE else View.GONE

        _binding.materialSwitchMultipleTimes.setOnCheckedChangeListener { _, isChecked ->
            TransitionManager.beginDelayedTransition(_binding.materialCardViewMultipleTimes)
            _binding.expandableContentMultipleTimes.visibility =
                if (isChecked) View.VISIBLE else View.GONE
        }

        _binding.materialSwitchSpecificDays.setOnCheckedChangeListener { _, isChecked ->
            TransitionManager.beginDelayedTransition(_binding.materialCardViewSpecificDaysOfWeek)
            _binding.expandableContentSpecificDays.visibility =
                if (isChecked) View.VISIBLE else View.GONE
        }
        _binding.materialSwitchCyclic.setOnCheckedChangeListener { _, isChecked ->
            TransitionManager.beginDelayedTransition(_binding.materialCardViewInterval)
            _binding.expandableContentCyclic.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        _binding.materialSwitchInterval.setOnCheckedChangeListener { _, isChecked ->
            TransitionManager.beginDelayedTransition(_binding.materialCardViewInterval)
            _binding.expandableContentInterval.visibility =
                if (isChecked) View.VISIBLE else View.GONE
        }

        _binding.radioGroupIntervalType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonHours -> {
                    _binding.layoutHoursInterval.visibility = View.VISIBLE
                    _binding.layoutDaysInterval.visibility = View.GONE
                }

                R.id.radioButtonDays -> {
                    _binding.layoutHoursInterval.visibility = View.GONE
                    _binding.layoutDaysInterval.visibility = View.VISIBLE
                }
            }
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.hours_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            _binding.spinnerHours.adapter = adapter
        }

        val days = (1..90).map { "$it" }.toTypedArray()
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, days).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            _binding.spinnerDays.adapter = it
        }

        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, days).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            _binding.spinnerDaysOn.adapter = it
        }

        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, days).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            _binding.spinnerDaysOff.adapter = it
        }

        _binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.ScheduleFragment_to_reminderFragment)
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.time_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            _binding.timesSpinner.adapter = adapter
        }


    }


}