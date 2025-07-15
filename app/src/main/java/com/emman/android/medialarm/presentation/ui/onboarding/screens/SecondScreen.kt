package com.emman.android.medialarm.presentation.ui.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.emman.android.medialarm.R
import com.emman.android.medialarm.databinding.FragmentSecondScreenBinding


class SecondScreen : Fragment() {

    private lateinit var _binding: FragmentSecondScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSecondScreenBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.tvNext.setOnClickListener {
            val parentFragment = parentFragment
            val viewPager = parentFragment?.view?.findViewById<ViewPager2>(R.id.viewPagerContainer)
            viewPager?.currentItem = 2
        }
    }
}
