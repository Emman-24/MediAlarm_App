package com.emman.android.medialarm.homeModule.user.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.emman.android.medialarm.databinding.FragmentUserBinding
import com.emman.android.medialarm.homeModule.user.viewModel.UserViewModel

class UserFragment : Fragment() {

    private lateinit var _binding: FragmentUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this)[UserViewModel::class.java]
        _binding.viewModel = viewModel
        _binding.lifecycleOwner = viewLifecycleOwner
        return _binding.root
    }
}