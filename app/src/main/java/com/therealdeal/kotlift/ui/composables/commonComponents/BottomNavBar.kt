package com.therealdeal.kotlift.ui.composables.commonComponents

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.therealdeal.kotlift.navigation.Route
import androidx.navigation.NavDestination.Companion.hasRoute

sealed class BottomNavItem(
    val route: Route,
    val label: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(Route.Home, "Home", Icons.Outlined.Home)
    data object Workouts : BottomNavItem(Route.Workouts, "Workouts", Icons.Outlined.FitnessCenter)
    data object Stats : BottomNavItem(Route.Stats(), "Stats", Icons.AutoMirrored.Outlined.TrendingUp)
    data object Run : BottomNavItem(Route.Run, "Run", Icons.AutoMirrored.Outlined.DirectionsRun)
    data object Profile : BottomNavItem(Route.Profile, "Profile", Icons.Outlined.Person)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Workouts,
    BottomNavItem.Stats,
    BottomNavItem.Run,
    BottomNavItem.Profile
)

@Composable
fun AppBottomNavBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var currentTab by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }

    LaunchedEffect(currentDestination) {
        bottomNavItems.forEach { item ->
            if (currentDestination?.hasRoute(item.route::class) == true) {
                currentTab = item
            }
        }
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = item == currentTab

            NavigationBarItem(
                selected = isSelected,
                alwaysShowLabel = true,
                onClick = {
                    if (isSelected) {
                        navController.popBackStack(
                            route = item.route::class,
                            inclusive = false
                        )
                    } else {
                        currentTab = item
                        navController.navigate(item.route) {
                            popUpTo(Route.Home) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        softWrap = false
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}