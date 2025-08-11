package com.emman.android.medialarm.presentation.ui.addMedicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.emman.android.medialarm.databinding.FragmentAddScheduleBinding
import com.emman.android.medialarm.presentation.viewmodels.AddMedineViewModel
import com.google.android.material.materialswitch.MaterialSwitch

private val SPINNER_INTERVAL_DAYS = (2..90).toList()
private val SPINNER_INTERVAL_HOURS = (1..12).toList()
private val SPINNER_MULTIPLE_TIMES = (2..10).toList()

class AddScheduleFragment : Fragment(), NumberPickerDialogFragment.NumberPickerListener {


    private val _viewModel: AddMedineViewModel by activityViewModels()
    private lateinit var _binding: FragmentAddScheduleBinding
    private var intakeDays = 30
    private var pauseDays = 10

    override fun onValuesSelected(intakeDays: Int, pauseDays: Int) {
        this.intakeDays = intakeDays
        this.pauseDays = pauseDays
        _viewModel.setIntakeDays(intakeDays)
        _viewModel.setPauseDays(pauseDays)
        updateIntakePauseText()
    }

    private fun updateIntakePauseText() {
        _binding.intakePauseTv.text = "$intakeDays intake, $pauseDays pause"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAddScheduleBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateIntakePauseText()
        adapterSwitches()

        _binding.btnIntervalDays.setOnClickListener { it ->
            updateSpinnerAdapter(_binding.spInterval, SPINNER_INTERVAL_DAYS)
        }

        _binding.btnIntervalHours.setOnClickListener {
            updateSpinnerAdapter(_binding.spInterval, SPINNER_INTERVAL_HOURS)
        }
        _binding.msMultiple.setOnClickListener {
            updateSpinnerAdapter(_binding.spMultiple, SPINNER_MULTIPLE_TIMES)
        }

        _binding.intakePauseTv.setOnClickListener {
            showNumberPickerDialog()
        }
    }

    private fun updateSpinnerAdapter(
        spinner: Spinner,
        data: List<Int>,
    ) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }


    private fun showNumberPickerDialog() {
        val dialogFragment = NumberPickerDialogFragment()
        dialogFragment.setNumberPickerListener(this)
        dialogFragment.setInitialValues(intakeDays, pauseDays)
        dialogFragment.show(parentFragmentManager, "NumberPickerDialog")
    }

    private fun setupSwitchListener(switch: MaterialSwitch, optionView: View) {
        switch.setOnCheckedChangeListener { _, isChecked ->
            optionView.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) {
                val allSwitches = listOf(
                    _binding.msInterval,
                    _binding.msMultiple,
                    _binding.msCyclic,
                    _binding.msSpecific
                )
                allSwitches.forEach { otherSwitch ->
                    if (otherSwitch != switch && otherSwitch.isChecked) {
                        otherSwitch.isChecked = false
                    }
                }
            }
        }
    }

    private fun adapterSwitches() {
        setupSwitchListener(_binding.msInterval, _binding.optionsInterval)
        setupSwitchListener(_binding.msMultiple, _binding.optionsMultiple)
        setupSwitchListener(_binding.msCyclic, _binding.optionsCyclic)
        setupSwitchListener(_binding.msSpecific, _binding.optionsSpecific)
    }
}
