package com.emman.android.medialarm.createModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emman.android.medialarm.databinding.ActivityCreateBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(_binding.root)
    }
}