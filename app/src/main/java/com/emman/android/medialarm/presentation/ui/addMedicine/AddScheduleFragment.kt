package com.emman.android.medialarm.presentation.ui.addMedicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.emman.android.medialarm.R
import com.emman.android.medialarm.data.local.entities.IntervalUnit
import com.emman.android.medialarm.databinding.FragmentAddScheduleBinding
import com.emman.android.medialarm.presentation.viewmodels.AddMedineViewModel
import com.google.android.material.materialswitch.MaterialSwitch

class AddScheduleFragment : Fragment(), NumberPickerDialogFragment.NumberPickerListener {

    private val viewModel: AddMedineViewModel by activityViewModels()
    private lateinit var _binding: FragmentAddScheduleBinding
    private var intakeDays = 30
    private var pauseDays = 10

    companion object {
        private val SPINNER_INTERVAL_DAYS = (2..90).toList()
        private val SPINNER_INTERVAL_HOURS = (1..12).toList()
        private val SPINNER_MULTIPLE_TIMES = (2..10).toList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddScheduleBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialState()
        setupObservers()
        setupClickListeners()
    }

    private fun setupInitialState() {
        updateIntakePauseText()
        setupSwitchListeners()
        initializeSpinners()
        restoreSpinnerSelections()

        if (viewModel.intervalUnit.value == null) {
            viewModel.setIntervalUnit(IntervalUnit.DAYS)
            updateIntervalButtonsState(isHoursSelected = false)
        } else {
            updateIntervalButtonsState(isHoursSelected = viewModel.intervalUnit.value == IntervalUnit.HOURS)
        }
    }

    private fun setupObservers() {
        viewModel.medicineNameUiState.observe(viewLifecycleOwner) { name ->
            _binding.medicineName.text = name
        }
    }

    private fun setupClickListeners() {
        with(_binding) {
            btnIntervalDays.setOnClickListener {
                updateSpinnerAdapter(spInterval, SPINNER_INTERVAL_DAYS)
                viewModel.setIntervalUnit(IntervalUnit.DAYS)
                updateIntervalButtonsState(isHoursSelected = false)
            }

            btnIntervalHours.setOnClickListener {
                updateSpinnerAdapter(spInterval, SPINNER_INTERVAL_HOURS)
                viewModel.setIntervalUnit(IntervalUnit.HOURS)
                updateIntervalButtonsState(isHoursSelected = true)
            }

            intakePauseTv.setOnClickListener {
                showNumberPickerDialog()
            }

            btnContinue.setOnClickListener {
                handleContinueButtonClick()
            }
        }
    }

    private fun updateIntervalButtonsState(isHoursSelected: Boolean) {
        with(_binding) {
            btnIntervalHours.isSelected = isHoursSelected
            btnIntervalDays.isSelected = !isHoursSelected
        }
    }

    private fun handleContinueButtonClick() {
        with(_binding) {
            when {
                msInterval.isChecked -> {
                    navigateToIntervalFragment()
                }

                msMultiple.isChecked -> {
                    saveMultipleTimesAndNavigate()
                }

                msCyclic.isChecked -> {
                    navigateToCyclicFragment()
                }

                msSpecific.isChecked -> {
                    navigateToSpecificFragment()
                }

                else -> {
                    showSelectOptionToast()
                }
            }
        }
    }

    private fun navigateToIntervalFragment() {
        with(_binding) {
            if (viewModel.intervalUnit.value == null) {
                Toast.makeText(
                    requireContext(),
                    "Please select days or hours for the interval",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            val selectedValue = spInterval.selectedItem.toString().toInt()
            when (viewModel.intervalUnit.value) {
                IntervalUnit.DAYS -> {
                    viewModel.setIntervalDays(selectedValue)
                    findNavController().navigate(R.id.addScheduleFragment_to_addTimesIntevalDaysFragment)
                }
                IntervalUnit.HOURS -> {
                    viewModel.setIntervalValue(selectedValue)
                    findNavController().navigate(R.id.addScheduleFragment_to_addTimesIntervalHoursFragment)
                }
                else -> {
                    Toast.makeText(
                        requireContext(),
                        "Please select days or hours for the interval",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun saveMultipleTimesAndNavigate() {
        viewModel.setMultipleTimesSchedule(
            _binding.spMultiple.selectedItem.toString().toInt()
        )
        findNavController().navigate(R.id.addScheduleFragment_to_addTimesMultipleFragment)
    }

    private fun navigateToCyclicFragment() {
        findNavController().navigate(R.id.addScheduleFragment_to_addCyclicFragment)
    }

    private fun navigateToSpecificFragment() {
        val selectedDays = mutableListOf<AddMedineViewModel.DayOfWeek>()
        with(_binding) {
            if (chipMonday.isChecked) selectedDays.add(AddMedineViewModel.DayOfWeek.MONDAY)
            if (chipTuesday.isChecked) selectedDays.add(AddMedineViewModel.DayOfWeek.TUESDAY)
            if (chipWednesday.isChecked) selectedDays.add(AddMedineViewModel.DayOfWeek.WEDNESDAY)
            if (chipThursday.isChecked) selectedDays.add(AddMedineViewModel.DayOfWeek.THURSDAY)
            if (chipFriday.isChecked) selectedDays.add(AddMedineViewModel.DayOfWeek.FRIDAY)
            if (chipSaturday.isChecked) selectedDays.add(AddMedineViewModel.DayOfWeek.SATURDAY)
            if (chipSunday.isChecked) selectedDays.add(AddMedineViewModel.DayOfWeek.SUNDAY)
        }
        viewModel.setSpecificDays(selectedDays)
        findNavController().navigate(R.id.addScheduleFragment_to_addTimesSpecificFragment)
    }

    private fun showSelectOptionToast() {
        Toast.makeText(
            requireContext(),
            "Please select an option",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun initializeSpinners() {
        updateSpinnerAdapter(_binding.spInterval, SPINNER_INTERVAL_DAYS)
        updateSpinnerAdapter(_binding.spMultiple, SPINNER_MULTIPLE_TIMES)
    }

    private fun restoreSpinnerSelections() {
        viewModel.multipleTimesSchedule.value?.let { times ->
            val position = SPINNER_MULTIPLE_TIMES.indexOf(times)
            if (position >= 0) {
                _binding.spMultiple.setSelection(position)
            }
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

    private fun setupSwitchListeners() {
        with(_binding) {
            setupSwitchListener(msInterval, optionsInterval)
            setupSwitchListener(msMultiple, optionsMultiple)
            setupSwitchListener(msCyclic, optionsCyclic)
            setupSwitchListener(msSpecific, optionsSpecific)
        }
    }

    private fun setupSwitchListener(switch: MaterialSwitch, optionView: View) {
        switch.setOnCheckedChangeListener { _, isChecked ->
            optionView.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) {
                uncheckOtherSwitches(switch)
            }
        }
    }

    private fun uncheckOtherSwitches(currentSwitch: MaterialSwitch) {
        val allSwitches = with(_binding) {
            listOf(msInterval, msMultiple, msCyclic, msSpecific)
        }

        allSwitches.forEach { otherSwitch ->
            if (otherSwitch != currentSwitch && otherSwitch.isChecked) {
                otherSwitch.isChecked = false
            }
        }
    }

    private fun showNumberPickerDialog() {
        val dialogFragment = NumberPickerDialogFragment()
        dialogFragment.setNumberPickerListener(this)
        dialogFragment.setInitialValues(intakeDays, pauseDays)
        dialogFragment.show(parentFragmentManager, "NumberPickerDialog")
    }

    override fun onValuesSelected(intakeDays: Int, pauseDays: Int) {
        this.intakeDays = intakeDays
        this.pauseDays = pauseDays
        updateIntakePauseText()
    }

    private fun updateIntakePauseText() {
        _binding.intakePauseTv.text = "$intakeDays intake, $pauseDays pause"
        viewModel.setIntakeDays(intakeDays)
        viewModel.setPauseDays(pauseDays)
    }

    override fun onResume() {
        super.onResume()
        initializeSpinners()
        restoreSpinnerSelections()
    }

}
