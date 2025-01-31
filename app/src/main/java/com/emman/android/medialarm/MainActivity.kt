package com.emman.android.medialarm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.emman.android.medialarm.common.adapters.SharedPreferencesHelper
import com.emman.android.medialarm.databinding.ActivityMainBinding
import com.emman.android.medialarm.homeModule.HomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private lateinit var _viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        _viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(sharedPreferencesHelper)
        )[MainViewModel::class.java]


        _viewModel.startDestination.observe(this) { startDestination ->
            if (startDestination) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        _viewModel.checkStartDestination()

    }

}
