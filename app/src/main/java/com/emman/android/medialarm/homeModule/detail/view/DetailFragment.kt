package com.emman.android.medialarm.homeModule.detail.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.emman.android.medialarm.databinding.FragmentDetailBinding
import com.emman.android.medialarm.homeModule.detail.viewmodel.DetailViewModel
import com.emman.android.medialarm.homeModule.detail.viewmodel.DetailViewModelFactory


class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()

    private lateinit var _binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        val medicineId = args.medicineId

        val viewModel =
            ViewModelProvider(this, DetailViewModelFactory(medicineId))[DetailViewModel::class.java]

        _binding.viewModel = viewModel

        _binding.lifecycleOwner = viewLifecycleOwner

        return _binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToNavigationList())
        }

        _binding.topAppBar.setOnClickListener {
            findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToNavigationList())
        }
    }


}