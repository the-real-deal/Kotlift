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
        popUpTo(0) {
            inclusive = true
        }
        launchSingleTop = true
    }
}
@Composable
fun NavGraph(navController: NavHostController, innerPadding: PaddingValues) {
    Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
        NavHost(
            navController = navController,
            startDestination = Route.Login
        ) {
            slideComposable<Route.Login> {
                LoginScreen(onNavigate = {nav -> when(nav) {
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
            slideComposable<Route.Register> {
                RegisterScreen { nav ->
                    when (nav) {
                        RegisterNavigation.Home -> navigateAndClear(
                            navController,
                            targetRoute = Route.Home
                        )

                        RegisterNavigation.Login -> navigateAndClear(
                            navController,
                            targetRoute = Route.Login
                        )
                    }
                }
            }
            slideComposable<Route.Home> {
                HomeScreen(onNavigate = { nav ->
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

                        is HomeNavigation.WorkoutDetail -> navigateOnStack(
                            navController,
                            targetRoute = Route.WorkoutDetail(workoutId = nav.id)
                        )

                        HomeNavigation.CreateWorkout -> navigateOnStack(
                            navController,
                            targetRoute = Route.CreateWorkout
                        )
                    }
                }, innerPadding = innerPadding)
            }

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
                    innerPadding = innerPadding,
                )
            }

            slideComposable<Route.WorkoutDetail> {
                WorkoutDetailScreen(
                    onNavigate = { nav ->
                        when (nav) {
                            WorkoutDetailNavigation.Back -> navController.popBackStack()
                            is WorkoutDetailNavigation.ExerciseDetail -> navigateOnStack(
                                navController,
                                targetRoute = Route.ExerciseDetail(nav.id)
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

            slideComposable<Route.Exercises> {
                ExercisesScreen(
                    { nav ->
                        when (nav) {
                            ExercisesNavigation.Back -> navController.popBackStack()
                            is ExercisesNavigation.ExerciseDetail -> navigateOnStack(
                                navController,
                                targetRoute = Route.ExerciseDetail(nav.id)
                            )
                        }
                    },
                    innerPadding = innerPadding
                )
            }

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

            slideComposable<Route.ActiveWorkout> {
                ActiveWorkoutScreen({ nav ->
                    when (nav) {
                        ActiveWorkoutNavigation.Back -> navController.popBackStack()
                    } 
                }, innerPadding)
            }

            slideComposable<Route.Stats> {
                StatsScreen(innerPadding = innerPadding)
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