package com.emman.android.medialarm.notificacionModule.view

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.emman.android.medialarm.databinding.FragmentNotificationBinding
import com.emman.android.medialarm.homeModule.HomeActivity
import com.emman.android.medialarm.notificacionModule.viewModel.NotificationViewModel
import com.emman.android.medialarm.notificacionModule.viewModel.PermissionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private lateinit var _binding: FragmentNotificationBinding
    private val _viewModel: NotificationViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            _viewModel.onPermissionResult(isGranted)
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = _viewModel
        }

        _binding.btnAccept.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                _viewModel.checkPermission(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                println("No need for notification permission on this device")
            }
        }

        lifecycleScope.launch {
            _viewModel.permissionState.collect { state ->
                when (state) {
                    is PermissionState.Request -> {
                        permissionLauncher.launch(state.permission)
                    }
                    PermissionState.Granted -> {
                        val intent = Intent(requireContext(), HomeActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }

                    PermissionState.Denied -> TODO()
                    PermissionState.Idle -> {}
                }
            }
        }



    }




}