package com.emman.android.medialarm.common.utils

sealed class NavDestination {
    data object ToHomeModule : NavDestination()
    data object ToWelcomeModule : NavDestination()
}