package com.emman.android.medialarm.createModule.reminder

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.emman.android.medialarm.databinding.FragmentReminderBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Locale


class ReminderFragment : Fragment() {

    private lateinit var _binding: FragmentReminderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /**
         * Update time text view
         */
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        _binding.timeTextView.text = timeFormat.format(System.currentTimeMillis())

        /**
         * Update date text view
         */
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        _binding.dateTextView.text = dateFormat.format(System.currentTimeMillis())

        /**
         * Set up time picker
         */
        _binding.selectTimeButton.setOnClickListener {
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setTitleText("Select Reminder Time")
                .build()


        }
    }

}