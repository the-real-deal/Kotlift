package com.therealdeal.kotlift.ui.screens.run

import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.data.repository.RunningRepository
import com.therealdeal.kotlift.model.RunSession
import com.therealdeal.kotlift.ui.baseAuthentication.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RunUiState (
    val runningSession : List<RunSession> = listOf(),
    val isLoading : Boolean = false
){
}
class RunViewModel(
    authRepository: AuthRepository,
    val runningRepository: RunningRepository
) : BaseViewModel(authRepository) {

    private val _uiState = MutableStateFlow(RunUiState())
    val uiState: StateFlow<RunUiState> = _uiState.asStateFlow()

    init {
        getRunningSessions()
    }

    fun getRunningSessions() {
        _uiState.update { it.copy(isLoading = true) }
        withAuth { user ->
            val sessions = runningRepository.getSession(user.id)
            _uiState.update { it.copy(isLoading = false, runningSession = sessions) }

        }
    }
}