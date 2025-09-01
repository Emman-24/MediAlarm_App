package com.emman.android.medialarm.presentation.ui.addMedicine.interval

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.emman.android.medialarm.R
import com.emman.android.medialarm.data.local.entities.IntervalUnit
import com.emman.android.medialarm.databinding.FragmentAddTimesIntervalHoursBinding
import com.emman.android.medialarm.presentation.ui.home.MenuActivity
import com.emman.android.medialarm.presentation.viewmodels.AddMedineViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddTimesIntervalHoursFragment : Fragment() {

    private lateinit var binding: FragmentAddTimesIntervalHoursBinding
    private val viewModel: AddMedineViewModel by activityViewModels()
    private var startHour: Int? = null
    private var startMinute: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddTimesIntervalHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()

        viewModel.setIntervalUnit(IntervalUnit.HOURS)
    }

    private fun setupObservers() {
        viewModel.medicineNameUiState.observe(viewLifecycleOwner) { name ->
            binding.medicineName.text = name
        }

        viewModel.pillCount.observe(viewLifecycleOwner) { count ->
            binding.pillsCountTextView.text = count.toString()
            binding.removePillButton.isEnabled = count > 1
        }

        viewModel.startDateInterval.observe(viewLifecycleOwner) { date ->
            if (date != null) {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.startDateEditText.setText(dateFormat.format(
                    Date.from(date.atZone(ZoneId.systemDefault()).toInstant())
                ))
            }
        }


        viewModel.saveResultInterval.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AddMedineViewModel.SaveResult.Success -> {
                    Toast.makeText(requireContext(), "Medicine saved successfully", Toast.LENGTH_SHORT).show()
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


    private fun setupClickListeners() {
        binding.startDateEditText.setOnClickListener {
            showDatePicker()
        }

        binding.startTimeTextView.setOnClickListener {
            showTimePicker()
        }

        binding.startDateLayout.setEndIconOnClickListener {
            showDatePicker()
        }

        binding.endTimeTextView.setOnClickListener {
            if (viewModel.intervalValue.value != null) {
                showEndTimeSelectionDialog(viewModel.intervalValue.value!!)
            } else {
                Toast.makeText(requireContext(), "Please set an interval value first", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addPillButton.setOnClickListener {
            viewModel.incrementPillCount()
        }

        binding.removePillButton.setOnClickListener {
            viewModel.decrementPillCount()
        }

        binding.btnSave.setOnClickListener {
            if (startHour == null || startMinute == null) {
                Toast.makeText(requireContext(), "Please select a start time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.endTimeTextView.text == "Select" || binding.endTimeTextView.text.isEmpty()) {
                Toast.makeText(requireContext(), "Please select an end time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.startDateEditText.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please select a start date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.saveMedicineIntervalHours()
        }
    }


    private fun showEndTimeSelectionDialog(intervalHours: Int) {
        if (startHour == null || startMinute == null) {
            Toast.makeText(requireContext(), "Please select a start time first", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val validTimes = calculateValidEndTimes(intervalHours)

        val dialogView = layoutInflater.inflate(R.layout.dialog_time_chips, null, false)
        val chipGroup =
            dialogView.findViewById<com.google.android.material.chip.ChipGroup>(R.id.timeChipGroup)

        validTimes.forEach { timeString ->
            val chip = Chip(requireContext())
            chip.text = timeString
            chip.isClickable = true
            chip.isCheckable = false
            chipGroup.addView(chip)
        }
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select End Time")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .show()

        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chip.setOnClickListener {
                binding.endTimeTextView.text = chip.text

                val timeText = chip.text.toString()
                parseTimeAndSaveToViewModel(timeText)

                dialog.dismiss()
            }
        }


    }

    private fun calculateValidEndTimes(intervalHours: Int): List<String> {
        val times = mutableListOf<String>()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, startHour!!)
            set(Calendar.MINUTE, startMinute!!)
        }
        val initialTimeMillis = calendar.timeInMillis
        var firstIteration = true
        while (true) {
            calendar.add(Calendar.HOUR_OF_DAY, intervalHours)
            if (!firstIteration && calendar.timeInMillis >= initialTimeMillis + 24 * 60 * 60 * 1000) {
                break
            }
            times.add(formatTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)))
            firstIteration = false

            if (times.size >= 24) break
        }
        if (times.isEmpty()) {
            calendar.add(Calendar.HOUR_OF_DAY, 24)
            times.add(formatTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)))
        }

        return times
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText("Select Start Time")
            .build()

        timePicker.addOnPositiveButtonClickListener {
            startHour = timePicker.hour
            startMinute = timePicker.minute

            val formattedTime = formatTime(startHour!!, startMinute!!)
            binding.startTimeTextView.text = formattedTime
            binding.endTimeTextView.text = "Select"

            val localTime = LocalTime.of(startHour!!, startMinute!!)
            viewModel.setStartIntervalTime(localTime)
        }
        timePicker.show(parentFragmentManager, "TIME_PICKER")
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(calendar.time)
    }

    private fun parseTimeAndSaveToViewModel(timeText: String) {
        try {
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = format.parse(timeText)
            if (date != null) {
                val calendar = Calendar.getInstance().apply {
                    time = date
                }
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                val localTime = LocalTime.of(hour, minute)
                viewModel.setEndIntervalTime(localTime)
            }
        } catch (e: Exception) {
            Log.e("AddTimesIntervalHoursFragment", "Error parsing time: ${e.message}")
            Toast.makeText(requireContext(), "Error parsing time", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker() {
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
        datePickerBuilder.setTitleText("Select Start Date")

        if (binding.startDateEditText.text.toString().isNotEmpty()) {
            try {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                val parsedDate = dateFormat.parse(binding.startDateEditText.text.toString())
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
            val selectedLocalDateTime: LocalDateTime =
                Instant.ofEpochMilli(selectedDateInMillis).atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            viewModel.setStartDateInterval(selectedLocalDateTime)

            /**
             * Update UI
             */
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val selectedDate = dateFormat.format(selectedDateInMillis)
            binding.startDateEditText.setText(selectedDate)
        }
        datePicker.addOnPositiveButtonClickListener {
            binding.startDateEditText.clearFocus()
        }
        datePicker.addOnDismissListener {
            binding.startDateEditText.clearFocus()
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }


}
