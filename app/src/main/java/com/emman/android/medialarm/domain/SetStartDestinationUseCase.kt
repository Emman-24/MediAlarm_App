package com.emman.android.medialarm.domain

import com.emman.android.medialarm.common.AppPreferences
import javax.inject.Inject

class SetStartDestinationUseCase @Inject constructor(
    private val preferences: AppPreferences
) {
    operator fun invoke(isTreatment: Boolean) = preferences.setStartDestination(isTreatment)
}

class GetStartDestinationUseCase @Inject constructor(
    private val preferences: AppPreferences
) {
    operator fun invoke(): Boolean = preferences.getStartDestination()
}