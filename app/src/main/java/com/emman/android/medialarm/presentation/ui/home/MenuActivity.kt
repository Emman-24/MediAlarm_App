package com.emman.android.medialarm.presentation.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.emman.android.medialarm.R
import com.emman.android.medialarm.databinding.ActivityMenuBinding
import com.emman.android.medialarm.presentation.ui.addMedicine.AddActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MenuActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMenuBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        _binding.fabMedicine.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }


        _binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.scheduleFragment -> {
                    navController.navigate(R.id.scheduleFragment)
                    true
                }

                R.id.historyFragment -> {
                    navController.navigate(R.id.historyFragment)
                    true
                }

                R.id.settingsFragment -> {
                    navController.navigate(R.id.settingsFragment)
                    true
                }

                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }

                else -> false
            }
        }
    }


}
