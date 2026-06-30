package com.therealdeal.kotlift.koin

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.therealdeal.kotlift.data.events.SessionEvents
import com.therealdeal.kotlift.data.repository.AchievementsRepository
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.data.repository.ExerciseDetailRepository
import com.therealdeal.kotlift.data.repository.ExerciseLibraryRepository
import com.therealdeal.kotlift.data.repository.SessionRepository
import com.therealdeal.kotlift.data.repository.StatsRepository
import com.therealdeal.kotlift.data.repository.DataStoreRepository
import com.therealdeal.kotlift.data.repository.RunningRepository
import com.therealdeal.kotlift.data.repository.WorkoutDetailRepository
import com.therealdeal.kotlift.data.repository.WorkoutRepository
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("theme")

val dataModule = module{
    single { get<Context>().dataStore }
    single<SessionEvents> { SessionEvents() }
    single<AuthRepository>{ AuthRepository(get()) }
    single<WorkoutRepository> { WorkoutRepository(get()) }
    single<SessionRepository> { SessionRepository(get(), get()) }
    single<StatsRepository> { StatsRepository(get()) }
    single<WorkoutDetailRepository> { WorkoutDetailRepository(get(), get()) }
    single<ExerciseDetailRepository> { ExerciseDetailRepository(get()) }
    single<ExerciseLibraryRepository> { ExerciseLibraryRepository(get()) }
    single<AchievementsRepository> { AchievementsRepository(get()) }
    single<DataStoreRepository> { DataStoreRepository(get()) }
    single<RunningRepository>{ RunningRepository(get()) }
}