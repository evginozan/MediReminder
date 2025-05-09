package com.evginozan.medireminder.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.evginozan.medireminder.core.navigation.Screen
import com.evginozan.medireminder.core.theme.Component

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationBar(
        modifier = modifier,
        containerColor = Component, // Add vibrant color
        contentColor = Color.Black
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Medication, contentDescription = "İlaç Takibi") },
            label = { Text("İlaçlar") },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.Home.route } == true,
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Tansiyon Takibi") },
            label = { Text("Tansiyon") },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.BloodPressure.route } == true,
            onClick = {
                navController.navigate(Screen.BloodPressure.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.WaterDrop, contentDescription = "Şeker Takibi") },
            label = { Text("Şeker") },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.BloodSugar.route } == true,
            onClick = {
                navController.navigate(Screen.BloodSugar.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}