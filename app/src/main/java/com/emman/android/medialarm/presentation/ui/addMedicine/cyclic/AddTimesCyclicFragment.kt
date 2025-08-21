package com.emman.android.medialarm.presentation.ui.addMedicine.cyclic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.emman.android.medialarm.databinding.FragmentAddTimesCyclicBinding
import com.emman.android.medialarm.domain.models.MedicationTime
import com.emman.android.medialarm.presentation.adapter.CyclicAdapter
import com.emman.android.medialarm.presentation.ui.home.MenuActivity
import com.emman.android.medialarm.presentation.viewmodels.AddMedineViewModel
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalTime


class AddTimesCyclicFragment : Fragment() {

    private lateinit var _binding: FragmentAddTimesCyclicBinding
    private val _viewModel: AddMedineViewModel by activityViewModels()
    private lateinit var adapter: CyclicAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAddTimesCyclicBinding.inflate(inflater, container, false)
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

        _binding.recyclerCyclic.apply {
            adapter = this@AddTimesCyclicFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        _binding.btnAddTime.setOnClickListener {
            adapter.addMedicationTime()
        }

        _binding.fabSave.setOnClickListener {
            val medicationTimes: List<MedicationTime> = adapter.getMedicationTimes()

            if (medicationTimes.isEmpty()) {
                Toast.makeText(requireContext(), "Please add at least one medication time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            _viewModel.medicationTimes.value = medicationTimes
            Log.d("medicationTimes", medicationTimes.toString())

            // Disable save button to indicate operation in progress
            _binding.fabSave.isEnabled = false

            // Save the medicine cycle
            _viewModel.saveMedicineCyclic()
        }

        // Observe save result
        _viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            // Re-enable save button
            _binding.fabSave.isEnabled = true

            when (result) {
                is AddMedineViewModel.SaveResult.Success -> {
                    // Show success message
                    Toast.makeText(requireContext(), "Medicine cycle saved successfully", Toast.LENGTH_SHORT).show()

                    // Navigate back to home
                    val intent = Intent(requireContext(), MenuActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                }
                is AddMedineViewModel.SaveResult.Error -> {
                    // Show error message
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
}
