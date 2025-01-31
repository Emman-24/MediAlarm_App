package com.emman.android.medialarm.homeModule.support.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.emman.android.medialarm.databinding.FragmentSupportBinding
import com.emman.android.medialarm.homeModule.support.viewModel.SupportViewModel

class SupportFragment : Fragment() {

    private lateinit var _binding: FragmentSupportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupportBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this)[SupportViewModel::class.java]
        _binding.viewModel = viewModel
        _binding.lifecycleOwner = viewLifecycleOwner
        return _binding.root

    }
}