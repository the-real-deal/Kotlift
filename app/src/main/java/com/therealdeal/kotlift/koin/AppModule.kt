package com.therealdeal.kotlift.koin

import com.therealdeal.kotlift.ui.screens.login.LoginViewModel
import com.therealdeal.kotlift.ui.screens.register.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
}