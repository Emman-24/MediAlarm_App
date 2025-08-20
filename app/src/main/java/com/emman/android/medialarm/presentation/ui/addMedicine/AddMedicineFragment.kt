package com.emman.android.medialarm.presentation.ui.addMedicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.emman.android.medialarm.R
import com.emman.android.medialarm.databinding.FragmentAddMedicineBinding
import com.emman.android.medialarm.presentation.viewmodels.AddMedineViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMedicineFragment : Fragment() {

    private lateinit var _binding: FragmentAddMedicineBinding
    private val _viewModel: AddMedineViewModel by activityViewModels()

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
            if (validateFields()) {
                with(_binding) {
                    validateMedicineName()
                    validateDosage()
                    validateUnit()
                    validateFormType()
                    validateNotes()

                    _viewModel.setMedicineName(tietMedicineName.text.toString())
                    _viewModel.setIsActive(switchActive.isChecked)
                }
                findNavController().navigate(R.id.addMedicineFragment_to_addScheduleFragment)
            }
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        if (!validateMedicineName()) isValid = false
        if (!validateDosage()) isValid = false
        if (!validateUnit()) isValid = false
        if (!validateFormType()) isValid = false
        if (!validateNotes()) isValid = false
        return isValid
    }

    private fun validateMedicineName(): Boolean {
        with(_binding) {
            val name = tietMedicineName.text.toString()
            val errorMessage = _viewModel.validateName(name)
            tilMedicineName.error = errorMessage
            return errorMessage == null
        }
    }

    private fun validateDosage(): Boolean {
        with(_binding) {
            val dosageText = tietDosage.text.toString()
            val errorMessage = _viewModel.validateDosage(dosageText)
            tilDosage.error = errorMessage
            return errorMessage == null
        }
    }

    private fun validateUnit(): Boolean {
        with(_binding) {
            val unit = actvUnit.text.toString()
            val errorMessage = _viewModel.validateUnit(unit)
            tilUnit.error = errorMessage
            return errorMessage == null
        }
    }

    private fun validateFormType(): Boolean {
        with(_binding) {
            val formType = actvFormType.text.toString()
            val errorMessage = _viewModel.validateFormType(formType)
            tilFormType.error = errorMessage
            return errorMessage == null
        }
    }

    private fun validateNotes(): Boolean {
        with(_binding) {
            val notes = tietNotes.text.toString()
            val errorMessage = _viewModel.validateNotes(notes)
            tilNotes.error = errorMessage
            return errorMessage == null
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
