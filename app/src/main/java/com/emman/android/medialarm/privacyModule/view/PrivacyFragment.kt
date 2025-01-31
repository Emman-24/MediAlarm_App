package com.emman.android.medialarm.privacyModule.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.emman.android.medialarm.common.adapters.SharedPreferencesHelper
import com.emman.android.medialarm.databinding.FragmentPrivacyBinding
import com.emman.android.medialarm.homeModule.HomeActivity
import com.emman.android.medialarm.privacyModule.viewModel.PrivacyViewModel


class PrivacyFragment : Fragment() {

    private lateinit var _binding: FragmentPrivacyBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivacyBinding.inflate(inflater, container, false)
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val viewModel = ViewModelProvider(this)[PrivacyViewModel::class.java]

        _binding.viewModel = viewModel
        _binding.lifecycleOwner = viewLifecycleOwner

        viewModel.navigateToHomeFragment.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                sharedPreferencesHelper.setStartDestination(true)
                val intent = Intent(requireContext(), HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                viewModel.onNavigateToHomeFragmentComplete()
            }
        }

        _binding.cbAccept.setOnCheckedChangeListener { _, isChecked ->
            _binding.btnAccept.isEnabled = isChecked
        }


        return _binding.root

    }

}