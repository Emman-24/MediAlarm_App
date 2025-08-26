package com.emman.android.medialarm.presentation.ui.addMedicine.specific

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.emman.android.medialarm.databinding.FragmentAddTimesSpecificBinding
import com.emman.android.medialarm.domain.models.MedicationTime
import com.emman.android.medialarm.presentation.adapter.CyclicAdapter
import com.emman.android.medialarm.presentation.ui.home.MenuActivity
import com.emman.android.medialarm.presentation.viewmodels.AddMedineViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalTime

class AddTimesSpecificFragment : Fragment() {

    private lateinit var _binding: FragmentAddTimesSpecificBinding
    private val viewModel: AddMedineViewModel by activityViewModels()
    private lateinit var adapter: CyclicAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddTimesSpecificBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()

        adapter = CyclicAdapter(onTimeClicked = { medicationTimes, position ->
            showTimePickerDialog(medicationTimes, position)
        })

        adapter.setMedicationTimes(
            listOf(
                MedicationTime(
                    time = LocalTime.now(),
                    amount = 1.0
                )
            )
        )
        _binding.timesRecyclerView.apply {
            adapter = this@AddTimesSpecificFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        _binding.fabAdd.setOnClickListener {
            adapter.addMedicationTime()
        }

        _binding.fabSave.setOnClickListener {
            val medicationTimes = adapter.getMedicationTimes()
            viewModel.medicationTimesSpecificDays.value = medicationTimes
            _binding.fabSave.isEnabled = false
            viewModel.saveMedicineSpecific()
        }

        viewModel.saveResultSpecific.observe(viewLifecycleOwner){result ->
            _binding.fabSave.isEnabled = true

            when(result){
                is AddMedineViewModel.SaveResult.Success -> {
                    Toast.makeText(requireContext(), "Medicine Specific saved successfully", Toast.LENGTH_SHORT).show()

                    // Navigate back to home
                    val intent = Intent(requireContext(), MenuActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is AddMedineViewModel.SaveResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
            }
        }

    }
    private fun setupObservers() {
        viewModel.uiStateMedicine.value.medicineName.let { name ->
            _binding.medicineName.text = name
        }
        viewModel.specificDays.observe(viewLifecycleOwner) { days ->
            val displayState = viewModel.getDisplayState(days)
            displaySelectedDays(displayState)
        }
    }

    private fun displaySelectedDays(state: AddMedineViewModel.DayDisplayState) {
        _binding.apply {
            when (state) {
                is AddMedineViewModel.DayDisplayState.Summary -> {
                    selectedDaysSummaryText.isVisible = true
                    selectedDaysChipGroup.isVisible = false
                    selectedDaysSummaryText.text = when {
                        state.stringRes != 0 -> getString(state.stringRes)
                        state.pluralRes != 0 -> resources.getQuantityString(
                            state.pluralRes,
                            state.quantity,
                            state.quantity
                        )

                        else -> ""
                    }
                }

                is AddMedineViewModel.DayDisplayState.Chips -> {
                    selectedDaysSummaryText.isVisible = false
                    selectedDaysChipGroup.isVisible = true
                    selectedDaysChipGroup.removeAllViews()
                    state.days.forEach { day ->
                        val chip = Chip(requireContext()).apply {
                            text = day.displayName
                            isCheckable = false
                        }
                        selectedDaysChipGroup.addView(chip)
                    }
                }
            }
        }
    }

    private fun showTimePickerDialog(medicationTime: MedicationTime, position: Int) {
        val currentTime = medicationTime.time
        val hour = currentTime.hour
        val minute = currentTime.minute

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hour)
            .setTitleText("Select Time")
            .setInputMode(INPUT_MODE_KEYBOARD)
            .setMinute(minute)
            .build()

        picker.show(parentFragmentManager, "tag")

        picker.addOnPositiveButtonClickListener {
            val newTime = LocalTime.of(picker.hour, picker.minute)
            val updatedMedicationTime = medicationTime.copy(time = newTime)
            adapter.updateMedicationTime(position, updatedMedicationTime)
        }
    }

}
