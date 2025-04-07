package com.emman.android.medialarm.createModule.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.emman.android.medialarm.R
import com.emman.android.medialarm.databinding.FragmentScheduleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleFragment : Fragment() {
    private lateinit var _binding: FragmentScheduleBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.ScheduleFragment_to_reminderFragment)
        }

    }


}