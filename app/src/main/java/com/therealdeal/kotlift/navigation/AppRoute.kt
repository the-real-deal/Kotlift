package com.therealdeal.kotlift.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Login : Route
    @Serializable data object Register : Route
    @Serializable data object Home : Route
    @Serializable data object Workouts : Route
    @Serializable data class WorkoutDetail(val workoutId: String) : Route
    @Serializable data class Exercises(val selectionMode: Boolean = false) : Route
    @Serializable data class ExerciseDetail(val exerciseId: String) : Route
    @Serializable data class Stats(val scrollToAllSessions: Boolean = false) : Route
    @Serializable data object Profile : Route
    @Serializable data object Run : Route
    @Serializable data object Running : Route
    @Serializable data class ActiveWorkout(val workoutId: String) : Route
    @Serializable data object CreateWorkout : Route
}

sealed class HomeNavigation(val route: Route?) {
    object Workouts : HomeNavigation(Route.Workouts)
    data class Stats(val scrollToAllSessions: Boolean = false) : HomeNavigation(Route.Stats(scrollToAllSessions))
    object Exercises : HomeNavigation(Route.Exercises())
    data class WorkoutDetail(val id: String) : HomeNavigation(Route.WorkoutDetail(id))
    object CreateWorkout : HomeNavigation(Route.CreateWorkout)
}

sealed class ActiveWorkoutNavigation(val route: Route?) {
    data class ExerciseDetail(val id: String) : ActiveWorkoutNavigation(Route.ExerciseDetail(id))
    object Back : ActiveWorkoutNavigation(null)
    object OpenExercisePicker : ActiveWorkoutNavigation(Route.Exercises(selectionMode = true))
}

sealed class ExerciseDetailNavigation(val route: Route?) {
    object Back : ExerciseDetailNavigation(null)
}

sealed class ExercisesNavigation(val route: Route?) {
    data class ExerciseDetail(val id: String) : ExercisesNavigation(Route.ExerciseDetail(id))
    object Back : ExercisesNavigation(null)
    data class ExerciseSelected(val exerciseId: String, val exerciseName: String) : ExercisesNavigation(null)
}

sealed class ProfileNavigation(val route: Route?) {
    object Login: ProfileNavigation(Route.Login)
}

sealed class StatsNavigation(val route: Route?) {
}

sealed class CreateNavigation(val route: Route?) {
    object Back : CreateNavigation(null)
    object OpenExercisePicker : CreateNavigation(Route.Exercises(selectionMode = true))
}

sealed class WorkoutDetailNavigation(val route: Route?) {
    data class ExerciseDetail(val id: String) : WorkoutDetailNavigation(Route.ExerciseDetail(id))
    data class ActiveWorkout(val id: String) : WorkoutDetailNavigation(Route.ActiveWorkout(id))
    object Back : WorkoutDetailNavigation(null)
}

sealed class WorkoutsNavigation(val route: Route?) {
    data class WorkoutDetail(val id: String) : WorkoutsNavigation(Route.WorkoutDetail(id))
}

sealed class RunNavigation(val route: Route?) {
    data object RunningNavigation : RunNavigation(Route.Running)
}

sealed class RunningNavigation {
    data object Back : RunningNavigation()
}

sealed class LoginNavigation(val route: Route?) {
    object Register : LoginNavigation(Route.Register)
    object Home : LoginNavigation(Route.Home)
}

sealed class RegisterNavigation(val route: Route?) {
    object Login : RegisterNavigation(Route.Login)
    object Home : RegisterNavigation(Route.Home)
}