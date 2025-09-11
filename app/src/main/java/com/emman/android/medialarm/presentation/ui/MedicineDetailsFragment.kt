package com.emman.android.medialarm.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.emman.android.medialarm.databinding.FragmentMedicineDetailsBinding
import com.emman.android.medialarm.presentation.viewmodels.MedicineViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MedicineDetailsFragment : Fragment() {
    private var _binding: FragmentMedicineDetailsBinding? = null
    private val binding get() = _binding!!

    private val medicineViewModel: MedicineViewModel by viewModels()
    private val args: MedicineDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMedicineDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        medicineViewModel.getMedicineDetails(args.id)

        observeMedicineDetails()

        binding.medicineNameToolbar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeMedicineDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            medicineViewModel.medicineDetails.collect { medicineDetails ->
                binding.medicine = medicineDetails
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
