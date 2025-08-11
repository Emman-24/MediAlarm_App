package com.emman.android.medialarm.presentation.ui.addMedicine

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.emman.android.medialarm.databinding.FragmentAddTimesMultipleBinding
import com.emman.android.medialarm.domain.models.MedicationTime
import com.emman.android.medialarm.presentation.adapter.MultipleAdapter
import com.emman.android.medialarm.presentation.viewmodels.AddMedineViewModel
import java.time.LocalTime


class AddTimesMultipleFragment : Fragment() {

    private lateinit var _binding: FragmentAddTimesMultipleBinding
    private val _viewModel: AddMedineViewModel by activityViewModels()
    private lateinit var adapter: MultipleAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddTimesMultipleBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _viewModel.medicineName.observe(viewLifecycleOwner) { name ->
            _binding.medicineName.text = name
        }

        val timesCount = _viewModel.multipleTimesSchedule.value ?: 2

        adapter = MultipleAdapter(timesCount) { medicationTime, position ->
            showTimePickerDialog(medicationTime, position)
        }

        _binding.recyclerMultiple.apply {
            this.adapter = this@AddTimesMultipleFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        _binding.fabSave.setOnClickListener {
            // Handle finish button click - can be implemented later
            // This would typically save the medication times to the ViewModel
        }
    }

    private fun showTimePickerDialog(medicationTime: MedicationTime, position: Int) {
        val currentTime = medicationTime.time
        val hour = currentTime.hour
        val minute = currentTime.minute

        TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val newTime = LocalTime.of(selectedHour, selectedMinute)
                val updatedMedicationTime = medicationTime.copy(time = newTime)
                adapter.updateMedicationTime(position, updatedMedicationTime)
            },
            hour,
            minute,
            true
        ).show()
    }
}
