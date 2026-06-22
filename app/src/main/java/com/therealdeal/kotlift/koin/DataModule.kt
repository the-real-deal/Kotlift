package com.therealdeal.kotlift.koin

import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.data.repository.SessionRepository
import com.therealdeal.kotlift.data.repository.StatsRepository
import com.therealdeal.kotlift.data.repository.WorkoutRepository
import org.koin.dsl.module

val dataModule = module{
    single<AuthRepository>{ AuthRepository(get()) }
    single<WorkoutRepository> { WorkoutRepository(get()) }
    single<SessionRepository> { SessionRepository(get()) }
    single<StatsRepository> { StatsRepository(get()) }
}