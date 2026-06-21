package com.therealdeal.kotlift

import android.app.Application
import com.therealdeal.kotlift.koin.appModule
import com.therealdeal.kotlift.koin.dataModule
import com.therealdeal.kotlift.koin.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(appModule, dataModule, networkModule)
        }
    }
}