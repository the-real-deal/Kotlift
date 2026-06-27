package com.therealdeal.kotlift.ui.screens.run

import android.content.Context
import android.content.Intent
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.service.RunningTrackingService
import com.therealdeal.kotlift.ui.baseAuthentication.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class RunningViewModel(
    authRepository: AuthRepository
) : BaseViewModel(authRepository) {

    val trackPoints = RunningTrackingService.track
    val isTracking = MutableStateFlow(false)


    fun startTracking(context: Context) {
        Intent(context, RunningTrackingService::class.java).also {
            context.startForegroundService(it)
        }
        isTracking.value = true
    }

    fun stopTracking(context: Context) {
        Intent(context, RunningTrackingService::class.java).also {
            context.stopService(it)
        }
        isTracking.value = false
    }

    fun clearTrack() {
        RunningTrackingService.clearTrack()
    }
}