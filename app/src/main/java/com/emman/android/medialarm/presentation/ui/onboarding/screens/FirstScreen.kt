package com.emman.android.medialarm.presentation.ui.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.emman.android.medialarm.databinding.FragmentFirstScreenBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FirstScreen : Fragment() {

    private lateinit var _binding: FragmentFirstScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentFirstScreenBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.tvNext.setOnClickListener {
            val parentFragment = parentFragment
            val viewPager = parentFragment?.view?.findViewById<androidx.viewpager2.widget.ViewPager2>(com.emman.android.medialarm.R.id.viewPagerContainer)
            viewPager?.currentItem = 1
        }
    }

}
