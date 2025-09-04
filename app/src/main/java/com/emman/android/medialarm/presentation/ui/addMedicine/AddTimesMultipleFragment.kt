package com.emman.android.medialarm.presentation.ui.addMedicine

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.emman.android.medialarm.databinding.FragmentAddTimesMultipleBinding
import com.emman.android.medialarm.domain.models.MedicationTime
import com.emman.android.medialarm.presentation.adapter.MultipleTimesAdapter
import com.emman.android.medialarm.presentation.ui.home.MenuActivity
import com.emman.android.medialarm.presentation.viewmodels.AddMedineViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale
import java.util.TimeZone


class AddTimesMultipleFragment : Fragment() {

    private lateinit var _binding: FragmentAddTimesMultipleBinding
    private val _viewModel: AddMedineViewModel by activityViewModels()
    private lateinit var adapter: MultipleTimesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddTimesMultipleBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.startDateEditText.showSoftInputOnFocus = false

        _viewModel.medicineNameUiState.observe(viewLifecycleOwner) { name ->
            _binding.medicineName.text = name
        }

        val timesCount = _viewModel.multipleTimesSchedule.value ?: 2

        adapter = MultipleTimesAdapter(
            timesCount,
            onTimeClicked = { medicationTime, position ->
                showTimePickerDialog(medicationTime, position)
            }
        )

        _binding.recyclerMultiple.apply {
            this.adapter = this@AddTimesMultipleFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        _binding.startDateEditText.setOnClickListener {
            showDatePicker()
        }


        _binding.fabSave.setOnClickListener {
            val medicationTimes = adapter.getMedicationTimes()
            if (medicationTimes.isNotEmpty()) {
                _viewModel.setMultipleTimesMedication(medicationTimes)
                _viewModel.saveMedicineMultiple()
                Snackbar.make(view, "Saving medication...", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(view, "Please add at least one time", Snackbar.LENGTH_SHORT).show()
            }
        }

        _viewModel.saveResultMultiple.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AddMedineViewModel.SaveResult.Success -> {
                    Snackbar.make(view, "Medication saved successfully", Snackbar.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), MenuActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is AddMedineViewModel.SaveResult.Error -> {
                    Snackbar.make(view, "Error: ${result.message}", Snackbar.LENGTH_LONG).show()
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

    fun showDatePicker() {
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
        datePickerBuilder.setTitleText("Select Start Date")

        if (_binding.startDateEditText.text.toString().isNotEmpty()) {
            try {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                val parsedDate = dateFormat.parse(_binding.startDateEditText.text.toString())
                parsedDate?.let {
                    datePickerBuilder.setSelection(it.time)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        val datePicker = datePickerBuilder.build()


        datePicker.addOnPositiveButtonClickListener { selectedDateInMillis ->
            /**
             * Update viewmodel
             */
            val selectedLocalDateTime = Instant.ofEpochMilli(selectedDateInMillis).atZone(ZoneId.systemDefault()).toLocalDateTime()
            _viewModel.setStartDateCyclic(selectedLocalDateTime)

            /**
             * Update UI
             */
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val selectedDate = dateFormat.format(selectedDateInMillis)
            _binding.startDateEditText.setText(selectedDate)
        }
        datePicker.addOnPositiveButtonClickListener {
            _binding.startDateEditText.clearFocus()
        }
        datePicker.addOnDismissListener {
            _binding.startDateEditText.clearFocus()
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }
}
