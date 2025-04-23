package com.emman.android.medialarm.createModule.searchView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.emman.android.medialarm.R
import com.emman.android.medialarm.createModule.CreateViewModel
import com.emman.android.medialarm.databinding.FragmentSearchViewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchViewFragment : Fragment() {

    private var _binding: FragmentSearchViewBinding? = null
    private val binding get() = _binding!!

    private val createViewModel: CreateViewModel by viewModels()
    private val viewModel: SearchViewModel by viewModels()

    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }


    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter { clickedMedication ->
            createViewModel.setNameMedicine(
                clickedMedication.medicineName
            )
            findNavController().navigate(R.id.searchViewFragment_to_ScheduleFragment)
        }
        binding.recyclerViewSearch.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }
    }

    private fun setupSearch() {
        binding.searchView.setupWithSearchBar(binding.searchBar)

        binding.searchView.editText.doOnTextChanged { text, _, _, _ ->
            viewModel.searchMedications(text.toString())
        }

        binding.searchView.addTransitionListener { searchView, previousState, newState ->
            if (newState == com.google.android.material.search.SearchView.TransitionState.HIDDEN) {
                // SearchView closed, potentially clear search or reset state
                viewModel.clearSearch()
            }
            if (newState == com.google.android.material.search.SearchView.TransitionState.SHOWN) {
                // SearchView opened, you could trigger an initial search if needed
                val currentQuery = binding.searchView.text.toString()
                if (currentQuery.isNotEmpty()) {
                    viewModel.searchMedications(currentQuery)
                }
            }
        }

    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            searchAdapter.submitList(results)
            binding.recyclerViewSearch.visibility =
                if (results.isEmpty()) View.GONE else View.VISIBLE
        }

//        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            binding.progressCircular.visibility = if (isLoading) View.VISIBLE else View.GONE
//        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewSearch.adapter = null
        _binding = null
    }

}
