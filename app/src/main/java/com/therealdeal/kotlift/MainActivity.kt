package com.therealdeal.kotlift

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hasRoute
//import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.therealdeal.kotlift.ui.composables.commonComponents.AppBottomNavBar
import com.therealdeal.kotlift.ui.theme.KotliftTheme
import com.therealdeal.kotlift.navigation.NavGraph
import com.therealdeal.kotlift.navigation.Route

class MainActivity : ComponentActivity() {
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            KotliftTheme {
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val showBottomBar = currentDestination?.let { dest ->
                            !dest.hasRoute(Route.Login::class) &&
                            !dest.hasRoute(Route.Register::class)
                } ?: false

                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomBar,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it })
                        ) {
                            AppBottomNavBar(navController)
                        }
                    }
                ) { innerPadding ->
                    NavGraph(navController = navController, innerPadding = innerPadding)
                }
            }
        }
    }
}