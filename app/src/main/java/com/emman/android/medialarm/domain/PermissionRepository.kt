package com.emman.android.medialarm.domain

interface PermissionRepository {
    fun hasNotificationPermission(permission: String): Boolean
}


















