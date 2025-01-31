package com.emman.android.medialarm.homeModule.treatment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.emman.android.medialarm.databinding.FragmentTreatmentBinding
import com.emman.android.medialarm.homeModule.treatment.viewModel.TreatmentViewModel


class TreatmentFragment : Fragment() {

    private lateinit var _binding: FragmentTreatmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTreatmentBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this)[TreatmentViewModel::class.java]
        _binding.viewModel = viewModel
        _binding.lifecycleOwner = viewLifecycleOwner

        return _binding.root

    }

}