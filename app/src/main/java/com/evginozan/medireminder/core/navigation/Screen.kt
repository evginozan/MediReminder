package com.evginozan.medireminder.core.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object MedicineDetail : Screen("medicine_detail/{medicineId}") {
        fun createRoute(medicineId: Long): String = "medicine_detail/$medicineId"
    }
    object BloodPressure : Screen("blood_pressure")
    object BloodSugar : Screen("blood_sugar")

    companion object {
        val bottomNavigationScreens = listOf(Home, BloodPressure, BloodSugar)
    }
}