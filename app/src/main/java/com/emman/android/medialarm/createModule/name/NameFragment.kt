package com.emman.android.medialarm.createModule.name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.emman.android.medialarm.R
import com.emman.android.medialarm.databinding.FragmentNameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NameFragment : Fragment() {
    private lateinit var _binding: FragmentNameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNameBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.addMedicineButton.setOnClickListener {
            findNavController().navigate(R.id.NameFragment_to_scheduleFragment)
        }
    }

}