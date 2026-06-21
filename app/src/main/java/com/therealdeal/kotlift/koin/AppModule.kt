package com.therealdeal.kotlift.koin

import com.therealdeal.kotlift.ui.screens.login.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    viewModel { LoginViewModel(get()) }
}