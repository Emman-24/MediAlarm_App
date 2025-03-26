package com.emman.android.medialarm.data.repository

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface PermissionRepository {
    fun hasNotificationPermission(permission: String): Boolean
}

class PermissionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
):PermissionRepository {
    override fun hasNotificationPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED
    }
}