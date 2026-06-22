package com.therealdeal.kotlift.koin

import com.therealdeal.kotlift.ui.screens.home.HomeViewModel
import com.therealdeal.kotlift.ui.screens.login.LoginViewModel
import com.therealdeal.kotlift.ui.screens.register.RegisterViewModel
import com.therealdeal.kotlift.ui.screens.stats.StatsViewModel
import com.therealdeal.kotlift.ui.screens.workouts.WorkoutsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { WorkoutsViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { StatsViewModel(get()) }
}