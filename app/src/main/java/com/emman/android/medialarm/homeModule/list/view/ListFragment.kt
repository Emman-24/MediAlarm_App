package com.emman.android.medialarm.homeModule.list.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.emman.android.medialarm.databinding.FragmentListBinding
import com.emman.android.medialarm.homeModule.list.adapter.RecyclerAdapter
import com.emman.android.medialarm.homeModule.list.adapter.RecyclerListener
import com.emman.android.medialarm.homeModule.list.viewModel.ListViewModel

class ListFragment : Fragment() {
    private lateinit var _binding: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        /**
         * Use viewmodelfactory to pass parameters to viewmodel class
         *  like datasource ,application etc.
         */

        val viewModel = ViewModelProvider(this)[ListViewModel::class.java]

        _binding.viewModel = viewModel

        _binding.lifecycleOwner = viewLifecycleOwner

        val recyclerView = _binding.medicalList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = RecyclerAdapter(RecyclerListener { medicineId ->
            viewModel.onMedicineClicked(medicineId)
            println("Medicamento seleccionado: $medicineId")
        })

        _binding.medicalList.adapter = adapter

        viewModel.listMedicines.observe(viewLifecycleOwner) { medicines ->
            adapter.submitList(medicines)
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner) { medicineId ->
            medicineId?.let {
                this.findNavController().navigate(
                    ListFragmentDirections.actionNavigationListToDetailFragment(medicineId)
                )
                viewModel.onDetailNavigated()
            }
        }



        return _binding.root
    }
}