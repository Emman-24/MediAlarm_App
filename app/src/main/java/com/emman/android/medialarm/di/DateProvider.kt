package com.emman.android.medialarm.di

import java.time.LocalDate

interface DateProvider {
    fun getCurrentDate(): LocalDate
}