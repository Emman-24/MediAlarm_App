package com.emman.android.medialarm.presentation.ui.home

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.emman.android.medialarm.R
import com.emman.android.medialarm.databinding.ActivityMenuBinding
import com.emman.android.medialarm.presentation.ui.addMedicine.AddActivity
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MenuActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMenuBinding
    private lateinit var navController: NavController

    // Navigation buttons
    private lateinit var homeButton: MaterialButton
    private lateinit var scheduleButton: MaterialButton
    private lateinit var historyButton: MaterialButton
    private lateinit var settingsButton: MaterialButton
    private lateinit var addButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        initializeButtons()
        setupDockedToolbar()
        setupDestinationChangeListener()
    }


    private fun initializeButtons() {
        homeButton = findViewById(R.id.docked_toolbar_home)
        scheduleButton = findViewById(R.id.docked_toolbar_schedule)
        historyButton = findViewById(R.id.docked_toolbar_history)
        settingsButton = findViewById(R.id.docked_toolbar_settings)
        addButton = findViewById(R.id.docked_toolbar_add_button)
    }

    private fun setupDockedToolbar() {

        _binding.dockedToolbarHome.setOnClickListener {
            navController.navigate(R.id.homeFragment)
        }

        _binding.dockedToolbarSchedule.setOnClickListener {
            navController.navigate(R.id.scheduleFragment)
        }

        _binding.dockedToolbarHistory.setOnClickListener {
            navController.navigate(R.id.historyFragment)
        }

        _binding.dockedToolbarSettings.setOnClickListener {
            navController.navigate(R.id.settingsFragment)
        }

        _binding.dockedToolbarAddButton.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        updateSelectedTab(R.id.homeFragment)
    }


    private fun setupDestinationChangeListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateSelectedTab(destination.id)
        }
    }

    private fun updateSelectedTab(destinationId: Int) {
        val defaultTint = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.md_theme_outline)
        )
        val selectedTint = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.md_theme_primary)
        )

        homeButton.setIconTint(defaultTint)
        scheduleButton.setIconTint(defaultTint)
        historyButton.setIconTint(defaultTint)
        settingsButton.setIconTint(defaultTint)

        // Highlight the selected tab
        when (destinationId) {
            R.id.homeFragment -> homeButton.setIconTint(selectedTint)
            R.id.scheduleFragment -> scheduleButton.setIconTint(selectedTint)
            R.id.historyFragment -> historyButton.setIconTint(selectedTint)
            R.id.settingsFragment -> settingsButton.setIconTint(selectedTint)
        }
    }

}
