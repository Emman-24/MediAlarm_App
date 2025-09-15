package com.emman.android.medialarm.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.emman.android.medialarm.databinding.FragmentPermissionBinding
import com.emman.android.medialarm.presentation.ui.home.MenuActivity


class PermissionFragment : Fragment() {

    private lateinit var binding: FragmentPermissionBinding
    val alarmPermissionResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.allowButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.setData(uri)
            alarmPermissionResultLauncher.launch(intent)
        }
        binding.skipButton.setOnClickListener {
            navigateToMainActivity()
        }
    }


    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MenuActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


}
