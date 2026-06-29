package com.therealdeal.kotlift.koin

import coil.ImageLoader
import coil.decode.GifDecoder
import com.therealdeal.kotlift.ui.screens.activeWorkout.ActiveWorkoutViewModel
import com.therealdeal.kotlift.ui.screens.exerciseDetail.ExerciseDetailViewModel
import com.therealdeal.kotlift.ui.screens.exercises.ExercisesViewModel
import com.therealdeal.kotlift.ui.screens.home.HomeViewModel
import com.therealdeal.kotlift.ui.screens.login.LoginViewModel
import com.therealdeal.kotlift.ui.screens.profile.ProfileViewModel
import com.therealdeal.kotlift.ui.screens.register.RegisterViewModel
import com.therealdeal.kotlift.ui.screens.run.RunViewModel
import com.therealdeal.kotlift.ui.screens.run.RunningViewModel
import com.therealdeal.kotlift.ui.screens.stats.StatsViewModel
import com.therealdeal.kotlift.ui.screens.workoutDetail.WorkoutDetailViewModel
import com.therealdeal.kotlift.ui.screens.workouts.WorkoutsViewModel
import com.therealdeal.kotlift.ui.theme.ThemeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        ImageLoader.Builder(androidContext())
            .components { add(GifDecoder.Factory()) }
            .build()
    }

    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { WorkoutsViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { StatsViewModel(get(), get()) }
    viewModel { WorkoutDetailViewModel(get(), get(), get()) }
    viewModel { ExerciseDetailViewModel(get(), get(), get()) }
    viewModel { ExercisesViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { ThemeViewModel(get()) }
    viewModel { ActiveWorkoutViewModel(
        get(), get(), get(), get(),get()) }
    viewModel { RunViewModel(get(), get()) }
    viewModel { RunningViewModel(get(), get()) }
}