package com.emman.android.medialarm

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.emman.android.medialarm.databinding.ActivityMainBinding
import com.emman.android.medialarm.presentation.ui.MenuActivity
import com.emman.android.medialarm.presentation.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val _viewModel: MainViewModel by viewModels()
    private lateinit var _binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var isNavigationSetUp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        _viewModel.acceptedUser.observe(this) { accepted ->
            if (accepted) {
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish()
            } else if (!isNavigationSetUp) {
                setupNavigation(navHostFragment)
                isNavigationSetUp = true
            }
        }
    }

    private fun setupNavigation(navHostFragment: NavHostFragment) {
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        navController.graph = graph
    }
}
