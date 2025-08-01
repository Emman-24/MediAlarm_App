package com.emman.android.medialarm.presentation.ui.addMedicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.emman.android.medialarm.R
import com.emman.android.medialarm.databinding.FragmentAddMedicineBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMedicineFragment : Fragment() {

    private lateinit var _binding: FragmentAddMedicineBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddMedicineBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        _binding.btnSaveMedicine.setOnClickListener {
            findNavController().navigate(R.id.addMedicineFragment_to_addScheduleFragment)
        }
    }

    private fun setupToolbar() {
        val toolbar = _binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Add Medicine"
        }
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}
