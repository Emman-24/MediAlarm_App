package com.emman.android.medialarm.presentation.ui.onboarding

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class OnboardingPageTransformer : ViewPager2.PageTransformer {
    
    private val MIN_SCALE = 0.85f
    private val MIN_ALPHA = 0.5f
    
    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width
        val pageHeight = page.height
        
        when {
            position < -1 -> {
                page.alpha = 0f
            }
            position <= 1 -> {
                val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
                val vertMargin = pageHeight * (1 - scaleFactor) / 2
                val horzMargin = pageWidth * (1 - scaleFactor) / 2
                
                if (position < 0) {
                    page.translationX = horzMargin - vertMargin / 2
                } else {
                    page.translationX = -horzMargin + vertMargin / 2
                }

                page.scaleX = scaleFactor
                page.scaleY = scaleFactor

                page.alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)

                page.rotationY = position * -30
            }
            else -> {
                page.alpha = 0f
            }
        }
    }
}