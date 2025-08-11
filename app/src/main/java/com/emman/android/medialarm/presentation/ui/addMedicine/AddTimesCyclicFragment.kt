package com.emman.android.medialarm.presentation.ui.addMedicine


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.emman.android.medialarm.databinding.FragmentAddTimesCyclicBinding
import com.emman.android.medialarm.presentation.viewmodels.AddMedineViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class AddTimesCyclicFragment : Fragment() {

    private lateinit var _binding: FragmentAddTimesCyclicBinding
    private val _viewModel: AddMedineViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAddTimesCyclicBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _viewModel.medicineName.observe(viewLifecycleOwner) { name ->
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
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val selectedDate = dateFormat.format(selectedDateInMillis)
            _binding.startDateEditText.setText(selectedDate)

            // _viewModel.setStartDate(selectedDateInMillis)
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
