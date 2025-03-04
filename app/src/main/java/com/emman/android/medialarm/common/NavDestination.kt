package com.emman.android.medialarm.common

sealed class NavDestination {
    data object ToHomeModule : NavDestination()
    data object ToWelcomeModule : NavDestination()
}