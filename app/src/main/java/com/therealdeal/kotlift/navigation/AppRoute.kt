package com.therealdeal.kotlift.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Login : Route
    @Serializable data object Register : Route
    @Serializable data object Home : Route
    @Serializable data object Workouts : Route
    @Serializable data object WorkoutDetail : Route
    @Serializable data object Exercises : Route
    @Serializable data object ExerciseDetail : Route
    @Serializable data object Stats : Route
    @Serializable data object Profile : Route
    @Serializable data object Run : Route
    @Serializable data object ActiveWorkout : Route
    @Serializable data object CreateWorkout : Route
}

sealed class HomeNavigation(val route: Route?) {
    object Workouts : HomeNavigation(Route.Workouts)
    object Stats : HomeNavigation(Route.Stats)
    object Exercises : HomeNavigation(Route.Exercises)
    object WorkoutDetail : HomeNavigation(Route.WorkoutDetail)
    object CreateWorkout : HomeNavigation(Route.CreateWorkout)
}

sealed class ActiveWorkoutNavigation(val route: Route?) {
    object Back : ActiveWorkoutNavigation(null)
}

sealed class ExerciseDetailNavigation(val route: Route?) {
    object Back : ExerciseDetailNavigation(null)
}

sealed class ExercisesNavigation(val route: Route?) {
    object ExerciseDetail : ExercisesNavigation(Route.ExerciseDetail)
    object Back : ExercisesNavigation(null)
}

sealed class ProfileNavigation(val route: Route?) {
}

sealed class StatsNavigation(val route: Route?) {
}

sealed class CreateNavigation(val route: Route?) {
    object Back : CreateNavigation(null)
}

sealed class WorkoutDetailNavigation(val route: Route?) {
    object ExerciseDetail : WorkoutDetailNavigation(Route.ExerciseDetail)
    object ActiveWorkout : WorkoutDetailNavigation(Route.ActiveWorkout)
    object Back : WorkoutDetailNavigation(null)
}

sealed class WorkoutsNavigation(val route: Route?) {
    data object WorkoutDetail : WorkoutsNavigation(Route.WorkoutDetail)
}

sealed class RunNavigation(val route: Route?) {
}

sealed class LoginNavigation(val route: Route?) {
    object Register : LoginNavigation(Route.Register)
    object Home : LoginNavigation(Route.Home)
}

sealed class RegisterNavigation(val route: Route?) {
    object Login : RegisterNavigation(Route.Login)
    object Home : RegisterNavigation(Route.Home)
}
