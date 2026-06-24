package com.therealdeal.kotlift.ui.screens.profile

import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.data.repository.AchievementsRepository
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.data.repository.ThemeRepository
import com.therealdeal.kotlift.model.Achievement
import com.therealdeal.kotlift.model.Profile
import com.therealdeal.kotlift.model.Theme
import com.therealdeal.kotlift.ui.baseAuthentication.BaseViewModel
import com.therealdeal.kotlift.ui.theme.ThemeState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState (
    val profile: Profile? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val achievements: List<Achievement> = emptyList(),
    val progress: Int = 0,

    val isLoadingProgress: Boolean = false
)

class ProfileViewModel(
    authRepository: AuthRepository,
    private val achievementsRepository: AchievementsRepository
) : BaseViewModel(authRepository) {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfileData() {
        withAuth { user ->
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val achievements = achievementsRepository.getAchievementsForUser(user.id)
            _uiState.value = _uiState.value.copy(
                profile = user,
                achievements = achievements,
                isLoading = false,
                errorMessage = null
            )
        }
    }

    fun loadProgress(
        achievement: Achievement
    ) {
        withAuth { user ->
            _uiState.value = _uiState.value.copy(isLoadingProgress = true, errorMessage = null)
            val progress = achievementsRepository.loadProgress(achievement, user.id)
            _uiState.update { it.copy(progress = progress, isLoadingProgress = false) }
        }
    }

    fun resetProgress() {
        _uiState.update { it.copy(progress = 0) }
    }
}