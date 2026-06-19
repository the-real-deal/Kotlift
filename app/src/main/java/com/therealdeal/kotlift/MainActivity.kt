package com.therealdeal.kotlift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
//import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.therealdeal.kotlift.ui.composables.commonComponents.AppBottomNavBar
import com.therealdeal.kotlift.ui.theme.KotliftTheme
import com.therealdeal.kotlift.navigation.NavGraph
import com.therealdeal.kotlift.navigation.Route

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            KotliftTheme {
                val navController = rememberNavController()

                // Osserva la destinazione attuale
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Scaffold(
                    bottomBar = {
                        // Usiamo il when per decidere se mostrare o meno la BottomBar
//                        when {
//                            // Se la destinazione è nulla, o corrisponde a Login/Register, non mostriamo nulla
//                            currentDestination == null -> {}
////                            currentDestination.hasRoute<Route.Login>() -> {}
////                            currentDestination.hasRoute<Route.Register>() -> {}
//
//                            // In tutti gli altri casi, mostra la BottomBar
//                            else ->
//                        }
                        AppBottomNavBar(navController)
                    }
                ) { innerPadding ->
                    NavGraph(navController = navController, innerPadding = innerPadding)
                }
            }
        }
    }
}