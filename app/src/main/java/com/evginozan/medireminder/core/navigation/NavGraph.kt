package com.evginozan.medireminder.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.evginozan.medireminder.presentation.bloodpressure.BloodPressureScreen
import com.evginozan.medireminder.presentation.bloodsugar.BloodSugarScreen
import com.evginozan.medireminder.presentation.detail.MedicineDetailScreen
import com.evginozan.medireminder.presentation.home.HomeScreen


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Ana sayfa
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDetail = { medicineId ->
                    navController.navigate(Screen.MedicineDetail.createRoute(medicineId))
                }
            )
        }

        // İlaç detay sayfası
        composable(
            route = Screen.MedicineDetail.route,
            arguments = listOf(
                navArgument("medicineId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val medicineId = backStackEntry.arguments?.getLong("medicineId") ?: 0L
            MedicineDetailScreen(
                medicineId = medicineId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Tansiyon takip sayfası
        composable(Screen.BloodPressure.route) {
            BloodPressureScreen()
        }

        // Şeker takip sayfası
        composable(Screen.BloodSugar.route) {
            BloodSugarScreen()
        }
    }
}