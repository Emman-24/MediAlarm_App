package com.emman.android.medialarm.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.emman.android.medialarm.databinding.FragmentPrivacyBinding
import com.emman.android.medialarm.presentation.ui.home.MenuActivity
import com.emman.android.medialarm.presentation.viewmodels.PrivacyPolicyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivacyFragment : Fragment() {

    private val _viewModel: PrivacyPolicyViewModel by viewModels()
    private lateinit var _binding: FragmentPrivacyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPrivacyBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.cbConditions.setOnCheckedChangeListener { _, isChecked ->
            _binding.btnContinue.isEnabled = isChecked
        }

        _binding.btnContinue.setOnClickListener {
            _viewModel.saveAcceptance(true)
            val intent = Intent(requireContext(), MenuActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }


    }


}