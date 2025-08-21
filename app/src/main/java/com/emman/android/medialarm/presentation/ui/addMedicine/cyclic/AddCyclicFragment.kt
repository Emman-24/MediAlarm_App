package com.emman.android.medialarm.presentation.ui.addMedicine.cyclic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.emman.android.medialarm.R
import com.emman.android.medialarm.databinding.FragmentAddCyclicBinding
import com.emman.android.medialarm.presentation.viewmodels.AddMedineViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import java.util.TimeZone

class AddCyclicFragment : Fragment() {

    private lateinit var _binding: FragmentAddCyclicBinding
    private val _viewModel: AddMedineViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAddCyclicBinding.inflate(inflater, container, false)
        return _binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Prevent keyboard from showing when date field is clicked
        _binding.startDateEditText.showSoftInputOnFocus = false

        _viewModel.medicineNameUiState.observe(viewLifecycleOwner) { name ->
            _binding.medicineName.text = name
        }

        _viewModel.intakeDays.observe(viewLifecycleOwner) { intakeDays ->
            _binding.tvIntakeDays.text = intakeDays.toString()
        }

        _viewModel.pauseDays.observe(viewLifecycleOwner) { pauseDays ->
            _binding.tvPauseDays.text = pauseDays.toString()
        }

        _binding.startDateEditText.setOnClickListener {
            showDatePicker()
        }

        _binding.btnContinueCycle.setOnClickListener {
            findNavController().navigate(R.id.action_addCyclicFragment_to_addTimesCyclicFragment)
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
            val selectedLocalDateTime =
                Instant.ofEpochMilli(selectedDateInMillis).atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            _viewModel.setStartDate(selectedLocalDateTime)

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
