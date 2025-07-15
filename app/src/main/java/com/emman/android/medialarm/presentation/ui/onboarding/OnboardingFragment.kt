package com.emman.android.medialarm.presentation.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.emman.android.medialarm.databinding.FragmentOnboardingBinding
import com.emman.android.medialarm.presentation.ui.onboarding.screens.FirstScreen
import com.emman.android.medialarm.presentation.ui.onboarding.screens.SecondScreen
import com.emman.android.medialarm.presentation.ui.onboarding.screens.ThirdScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private lateinit var _binding: FragmentOnboardingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentList = arrayListOf(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen()
        )

        val adapter = OnBoardingAdapter(
            fragmentList,
            childFragmentManager,
            viewLifecycleOwner.lifecycle
        )

        val viewPager2 = _binding.viewPagerContainer
        viewPager2.adapter = adapter


    }


}