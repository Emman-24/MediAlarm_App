package com.emman.android.medialarm.presentation.ui.addMedicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.emman.android.medialarm.databinding.FragmentAddTimesSpecificBinding
import com.emman.android.medialarm.presentation.viewmodels.AddMedineViewModel


class AddTimesSpecificFragment : Fragment() {

    private lateinit var _binding: FragmentAddTimesSpecificBinding
    private val _viewModel: AddMedineViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAddTimesSpecificBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}