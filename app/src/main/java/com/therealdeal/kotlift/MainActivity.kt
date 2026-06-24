package com.therealdeal.kotlift

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.therealdeal.kotlift.model.Theme
import com.therealdeal.kotlift.ui.composables.commonComponents.AppBottomNavBar
import com.therealdeal.kotlift.ui.theme.KotliftTheme
import com.therealdeal.kotlift.navigation.NavGraph
import com.therealdeal.kotlift.navigation.Route
import com.therealdeal.kotlift.ui.screens.profile.ProfileViewModel
import com.therealdeal.kotlift.ui.theme.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val themeState by themeViewModel.state.collectAsStateWithLifecycle()

            KotliftTheme(
                darkTheme = when (themeState.theme) {
                Theme.Light -> false
                Theme.Dark -> true
                Theme.System -> isSystemInDarkTheme()
            }) {
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
                    NavGraph(navController = navController, innerPadding = innerPadding, currentTheme = themeState.theme) { theme ->
                        themeViewModel.setTheme(theme)
                    }
                }
            }
        }
    }
}