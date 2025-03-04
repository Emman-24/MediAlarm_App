package com.emman.android.medialarm.privacyModule.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emman.android.medialarm.R
import com.emman.android.medialarm.databinding.FragmentPrivacyBinding
import com.emman.android.medialarm.privacyModule.viewModel.PrivacyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivacyFragment : Fragment() {

    private lateinit var _binding: FragmentPrivacyBinding
    private val _viewModel: PrivacyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivacyBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this)[PrivacyViewModel::class.java]
        _binding.lifecycleOwner = viewLifecycleOwner
        _binding.viewModel = viewModel
        return _binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _viewModel.navigationEvent.observe(viewLifecycleOwner) { isTermsAccepted ->
            if (isTermsAccepted) {
                findNavController().navigate(R.id.action_privacyFragment_to_notificationFragment)
            }

        }

        _binding.cbAccept.setOnCheckedChangeListener { _, isChecked ->
            _viewModel.termsAccepted.value = isChecked
        }

    }

}