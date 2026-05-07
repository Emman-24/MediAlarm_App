package com.emman.android.medialarmapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val backStack = rememberNavBackStack(Routes.Home)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryProvider = entryProvider {

            entry<Routes.Home> {

            }

            entry<Routes.MedicineList> {

            }

            entry<Routes.MedicineDetail> {

            }

            entry<Routes.ScheduleCreation> {

            }

            entry<Routes.AddMedicine> {

            }

            entry<Routes.Error> {

            }

        }
    )
}