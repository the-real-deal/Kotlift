package com.therealdeal.kotlift.data.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SessionEvents {
    private val _sessionCreated = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionCreated = _sessionCreated.asSharedFlow()
    suspend fun notifySessionCreated() = _sessionCreated.emit(Unit)
}