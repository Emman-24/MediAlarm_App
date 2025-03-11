package com.emman.android.medialarm.homeModule.list.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.emman.android.medialarm.databinding.FragmentListBinding
import com.emman.android.medialarm.homeModule.list.adapter.RecyclerAdapter
import com.emman.android.medialarm.homeModule.list.adapter.RecyclerListener
import com.emman.android.medialarm.homeModule.list.viewModel.ListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment() {
    private lateinit var _binding: FragmentListBinding
    private val _viewModel: ListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = _viewModel
        }

        val recyclerView = _binding.medicalList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = RecyclerAdapter(RecyclerListener { medicineId ->
            _viewModel.onMedicineClicked(medicineId)
            println("Medicamento seleccionado: $medicineId")
        })

        recyclerView.adapter = adapter

        _viewModel.listMedicines.observe(viewLifecycleOwner) { medicines ->
            adapter.submitList(medicines)
        }


        _viewModel.navigateToDetail.observe(viewLifecycleOwner) { medicineId ->
            medicineId?.let {
                this.findNavController().navigate(
                    ListFragmentDirections.actionNavigationListToDetailFragment(medicineId)
                )
                _viewModel.onDetailNavigated()
            }
        }


    }
}