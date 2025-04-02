package com.emman.android.medialarm.homeModule

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.emman.android.medialarm.R
import com.emman.android.medialarm.createModule.CreateActivity
import com.emman.android.medialarm.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        val bottomNavigationView = _binding.bottomNavigationView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_home) as NavHostFragment
        val navController = navHostFragment.navController

        if (viewModel.isMedicinesListEmpty) {
            bottomNavigationView.visibility = View.GONE
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
            finish()

        } else {
            bottomNavigationView.visibility = View.VISIBLE
            navController.navigate(R.id.navigation_treatment)
        }


        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.navigation_treatment -> {
                    navController.navigate(R.id.navigation_treatment)
                    true
                }

                R.id.navigation_list -> {
                    navController.navigate(R.id.navigation_list)
                    true
                }

                R.id.navigation_user -> {
                    true
                }

                R.id.navigation_support -> {
                    true
                }

                else -> false

            }
        }

    }

}