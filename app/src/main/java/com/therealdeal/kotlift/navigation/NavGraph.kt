package com.therealdeal.kotlift.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.therealdeal.kotlift.ui.screens.activeWorkout.ActiveWorkoutScreen
import com.therealdeal.kotlift.ui.screens.createWorkout.CreateWorkoutScreen
import com.therealdeal.kotlift.ui.screens.exerciseDetail.ExerciseDetailScreen
import com.therealdeal.kotlift.ui.screens.exercises.ExercisesScreen
import com.therealdeal.kotlift.ui.screens.home.HomeScreen
import com.therealdeal.kotlift.ui.screens.login.LoginScreen
import com.therealdeal.kotlift.ui.screens.profile.ProfileScreen
import com.therealdeal.kotlift.ui.screens.register.RegisterScreen
import com.therealdeal.kotlift.ui.screens.run.RunScreen
import com.therealdeal.kotlift.ui.screens.stats.StatsScreen
import com.therealdeal.kotlift.ui.screens.workoutDetail.WorkoutDetailScreen
import com.therealdeal.kotlift.ui.screens.workouts.WorkoutsScreen

fun navigateOnStack(navController: NavHostController, targetRoute: Route) {
    navController.navigate(targetRoute)
}

fun navigateAndClear(navController: NavHostController, targetRoute: Route) {
    navController.navigate(targetRoute) {
//        NavOptionsBuilder.popUpTo(Route.Home) {
//            PopUpToBuilder.saveState = true
//        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun NavGraph(navController: NavHostController, innerPadding: PaddingValues) {
    Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
        NavHost(
            navController = navController,
            startDestination = Route.Login
        ) {
            // --- LOGIN SCREEN ---
            slideComposable<Route.Login> {
                LoginScreen({nav -> when(nav) {
                    LoginNavigation.Home -> navigateAndClear(
                        navController,
                        targetRoute = Route.Home
                    )
                    LoginNavigation.Register -> navigateAndClear(
                        navController,
                        targetRoute = Route.Register
                    )
                } })
            }
            // --- REGISTER SCREEN ---
            slideComposable<Route.Register> {
                RegisterScreen({nav -> when(nav) {
                    RegisterNavigation.Home -> navigateAndClear(
                        navController,
                        targetRoute = Route.Home
                    )
                    RegisterNavigation.Login -> navigateAndClear(
                        navController,
                        targetRoute = Route.Login
                    )
                } })
            }
            // --- HOME SCREEN ---
            slideComposable<Route.Home> {
                HomeScreen({ nav ->
                    when (nav) {
                        HomeNavigation.Workouts -> navigateAndClear(
                            navController,
                            targetRoute = Route.Workouts
                        )

                        HomeNavigation.Stats -> navigateAndClear(
                            navController,
                            targetRoute = Route.Stats
                        )
                        HomeNavigation.Exercises -> navigateOnStack(
                            navController,
                            targetRoute = Route.Exercises
                        )

                        HomeNavigation.WorkoutDetail -> navigateOnStack(
                            navController,
                            targetRoute = Route.WorkoutDetail
                        )

                        HomeNavigation.CreateWorkout -> navigateOnStack(
                            navController,
                            targetRoute = Route.CreateWorkout
                        )
                    }
                }, innerPadding)
            }

            // --- WORKOUTS SCREEN ---
            slideComposable<Route.Workouts> {


                WorkoutsScreen(
                    onNavigate = { nav ->
                        when (nav) {
                            is WorkoutsNavigation.WorkoutDetail -> navigateOnStack(
                                navController,
                                targetRoute = nav.route!!
                            )
                        }
                    },
                    innerPadding = innerPadding
                )
            }

// --- WORKOUT DETAIL SCREEN ---
            slideComposable<Route.WorkoutDetail> {

                WorkoutDetailScreen(
                    onNavigate = { nav ->
                        when (nav) {
                            WorkoutDetailNavigation.Back -> navController.popBackStack()
                            WorkoutDetailNavigation.ExerciseDetail -> navigateOnStack(
                                navController,
                                targetRoute = Route.Exercises
                            )
                            WorkoutDetailNavigation.ActiveWorkout -> navigateOnStack(
                                navController,
                                targetRoute = Route.ActiveWorkout
                            )
                        }
                    },
                    innerPadding = innerPadding
                )
            }

            // --- EXERCISES SCREEN (DATABASE) ---
            slideComposable<Route.Exercises> {
                ExercisesScreen(
                    { nav ->
                        when (nav) {
                            ExercisesNavigation.Back -> navController.popBackStack()
                            ExercisesNavigation.ExerciseDetail -> navigateOnStack(
                                navController,
                                targetRoute = Route.ExerciseDetail
                            )
                        }
                    },
                    innerPadding = innerPadding
                )
            }

            // --- EXERCISE DETAIL SCREEN ---
            slideComposable<Route.ExerciseDetail> {
                ExerciseDetailScreen(
                    { nav ->
                        when (nav) {
                            ExerciseDetailNavigation.Back -> navController.popBackStack()
                        }
                    },
                    innerPadding = innerPadding
                )
            }

            // --- ACTIVE WORKOUT SCREEN ---
            slideComposable<Route.ActiveWorkout> {
                ActiveWorkoutScreen({ nav ->
                    when (nav) {
                        ActiveWorkoutNavigation.Back -> navController.popBackStack()
                    }
                }, innerPadding)
            }

            // --- ALTRI SCHERMI DI BASE ---
            slideComposable<Route.Stats> {
                StatsScreen({}, innerPadding)
            }

            slideComposable<Route.Profile> {
                ProfileScreen({}, innerPadding)
            }

            slideComposable<Route.Run> {
                RunScreen({}, innerPadding)
            }

            slideComposable<Route.CreateWorkout> {
                CreateWorkoutScreen({}, innerPadding)
            }
        }
    }
}