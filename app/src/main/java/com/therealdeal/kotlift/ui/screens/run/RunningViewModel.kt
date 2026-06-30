package com.therealdeal.kotlift.ui.screens.run

import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.data.repository.RunningRepository
import com.therealdeal.kotlift.service.RunningTrackingService
import com.therealdeal.kotlift.ui.baseAuthentication.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RunningViewModel(
    authRepository: AuthRepository,
    val runningRepository: RunningRepository
) : BaseViewModel(authRepository) {

    val trackPoints = RunningTrackingService.track
    val isTracking = MutableStateFlow(false)

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow()

    private var timerJob: Job? = null

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _elapsedSeconds.value++
            }
        }
    }

    fun stopTimer() { timerJob?.cancel() }

    fun startTracking(context: Context) {
        Intent(context, RunningTrackingService::class.java).also {
            context.startForegroundService(it)
        }
        startTimer()
        isTracking.value = true
    }

    fun stopTracking(context: Context) {
        Intent(context, RunningTrackingService::class.java).also {
            context.stopService(it)
        }
        stopTimer()
        isTracking.value = false
    }

    fun saveRun(){
        withAuth { user ->
            runningRepository.publishNewSession(user.id, RunningTrackingService.toSession())
        }
    }

    fun clearTrack() {
        _elapsedSeconds.value = 0
        RunningTrackingService.clearTrack()
    }
}