package com.therealdeal.kotlift.koin

import com.therealdeal.kotlift.data.repository.AuthRepository
import org.koin.dsl.module

val dataModule = module{
    single<AuthRepository>{ AuthRepository(get()) }
}