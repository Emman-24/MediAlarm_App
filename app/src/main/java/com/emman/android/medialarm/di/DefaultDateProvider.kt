package com.emman.android.medialarm.di

import java.time.LocalDate
import javax.inject.Inject

class DefaultDateProvider @Inject constructor() : DateProvider {
    override fun getCurrentDate(): LocalDate = LocalDate.now()
}