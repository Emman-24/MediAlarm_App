package com.emman.android.medialarm.privacyModule.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.emman.android.medialarm.R
import com.emman.android.medialarm.databinding.FragmentPrivacyBinding
import com.emman.android.medialarm.homeModule.HomeActivity
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
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = _viewModel
        }

        _binding.cbAccept.setOnCheckedChangeListener { _, isChecked ->
            _viewModel.termsAccepted.value = isChecked
        }

        _viewModel.navigationEvent.observe(viewLifecycleOwner) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                findNavController().navigate(R.id.action_privacyFragment_to_notificationFragment)
            } else {
                val intent = Intent(requireContext(), HomeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

        }

    }

}