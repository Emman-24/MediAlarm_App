package com.emman.android.medialarm.presentation.ui.addMedicine.interval

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.emman.android.medialarm.databinding.FragmentAddTimesIntevalDaysBinding
import com.emman.android.medialarm.domain.models.MedicationTime
import com.emman.android.medialarm.presentation.adapter.CyclicAdapter
import com.emman.android.medialarm.presentation.ui.home.MenuActivity
import com.emman.android.medialarm.presentation.viewmodels.AddMedineViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale
import java.util.TimeZone


class AddTimesIntervalDaysFragment : Fragment() {
    private lateinit var _binding: FragmentAddTimesIntevalDaysBinding
    private val _viewModel: AddMedineViewModel by activityViewModels()
    private lateinit var adapter: CyclicAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAddTimesIntevalDaysBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _viewModel.medicineNameUiState.observe(viewLifecycleOwner) { name ->
            _binding.medicineName.text = name
        }

        adapter = CyclicAdapter(onTimeClicked = { medicationTime, position ->
            showTimePickerDialog(medicationTime, position)
        })

        adapter.setMedicationTimes(
            listOf(
                MedicationTime(time = LocalTime.now(), amount = 1.0)
            )
        )

        _binding.reminderTimesList.apply {
            adapter = this@AddTimesIntervalDaysFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        _binding.fabAdd.setOnClickListener {
            adapter.addMedicationTime()
        }

        _binding.btnStartDate.setOnClickListener {
            showDatePicker()
        }

        _binding.fabSave.setOnClickListener {
            val medicationTimes: List<MedicationTime> = adapter.getMedicationTimes()
            _viewModel.medicationTimesIntervalDays.value = medicationTimes
            _viewModel.saveMedicineIntervalDays()
        }

        _viewModel.saveResultInterval.observe(viewLifecycleOwner) { result ->
            when (result) {
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

private fun showDatePicker() {
    val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
    datePickerBuilder.setTitleText("Select Start Date")

    val datePicker = datePickerBuilder.build()

    datePicker.addOnPositiveButtonClickListener { selectedDateInMillis ->
        /**
         * Update viewmodel
         */
        val selectedLocalDateTime =  Instant.ofEpochMilli(selectedDateInMillis).atZone(ZoneId.systemDefault()).toLocalDateTime()
        _viewModel.setStartDateInterval(selectedLocalDateTime)

        /**
         * Update view
         */
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val selectedDate = dateFormat.format(selectedDateInMillis)
        _binding.btnStartDate.text = selectedDate
    }

    datePicker.show(parentFragmentManager, "DATE_PICKER")

}

}