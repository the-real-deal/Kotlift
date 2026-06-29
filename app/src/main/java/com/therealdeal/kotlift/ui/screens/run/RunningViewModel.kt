package com.therealdeal.kotlift.ui.screens.run

import android.content.Context
import android.content.Intent
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.data.repository.RunningRepository
import com.therealdeal.kotlift.model.RunSession
import com.therealdeal.kotlift.service.RunningTrackingService
import com.therealdeal.kotlift.ui.baseAuthentication.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Clock

class RunningViewModel(
    authRepository: AuthRepository,
    val runningRepository: RunningRepository
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

    fun saveRun(){
        withAuth { user ->
            runningRepository.publishNewSession(user.id, RunningTrackingService.toSession())
        }
    }

    fun clearTrack() {
        RunningTrackingService.clearTrack()
    }
}