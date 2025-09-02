package com.emman.android.medialarm.presentation.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.emman.android.medialarm.R
import com.emman.android.medialarm.databinding.FragmentOnboardingBinding
import com.emman.android.medialarm.presentation.ui.onboarding.screens.FirstScreen
import com.emman.android.medialarm.presentation.ui.onboarding.screens.SecondScreen
import com.emman.android.medialarm.presentation.ui.onboarding.screens.ThirdScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private lateinit var _binding: FragmentOnboardingBinding
    private val indicators = mutableListOf<View>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup indicators
        indicators.add(_binding.indicator1)
        indicators.add(_binding.indicator2)
        indicators.add(_binding.indicator3)

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

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)
            }
        })

        viewPager2.setPageTransformer(OnboardingPageTransformer())

        _binding.btnSkip.setOnClickListener {
            findNavController().navigate(R.id.onboardingFragment_to_privacyFragment)
        }
    }

    private fun updateIndicators(position: Int) {
        indicators.forEachIndexed { index, view ->
            view.background = if (index == position) {
                resources.getDrawable(R.drawable.indicator_active, requireContext().theme)
            } else {
                resources.getDrawable(R.drawable.indicator_inactive, requireContext().theme)
            }
        }
    }
}
