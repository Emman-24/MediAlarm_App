package com.emman.android.medialarm.homeModule.list.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.emman.android.medialarm.databinding.FragmentListBinding
import com.emman.android.medialarm.homeModule.list.viewModel.ListViewModel

class ListFragment : Fragment() {
    private lateinit var _binding: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this)[ListViewModel::class.java]
        _binding.viewModel = viewModel
        _binding.lifecycleOwner = viewLifecycleOwner
        return _binding.root
    }
}