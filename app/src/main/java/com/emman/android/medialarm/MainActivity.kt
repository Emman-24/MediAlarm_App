package com.emman.android.medialarm

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.emman.android.medialarm.common.NavDestination
import com.emman.android.medialarm.databinding.ActivityMainBinding
import com.emman.android.medialarm.homeModule.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val _viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        _viewModel.checkStartDestination()

        _viewModel.navigationEvent.observe(this) { event ->
            when (event) {
                is NavDestination.ToHomeModule -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                NavDestination.ToWelcomeModule -> {
                    navController.navigate(R.id.welcomeFragment)
                }
            }
        }


    }

}
