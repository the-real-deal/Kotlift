package com.therealdeal.kotlift.ui.screens.profile

import android.net.Uri
import com.therealdeal.kotlift.data.repository.AchievementsRepository
import com.therealdeal.kotlift.data.repository.AuthRepository
import com.therealdeal.kotlift.data.repository.DataStoreRepository
import com.therealdeal.kotlift.model.Achievement
import com.therealdeal.kotlift.model.Profile
import com.therealdeal.kotlift.ui.baseAuthentication.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState(
    val profile: Profile? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val achievements: List<Achievement> = emptyList(),
    val progress: Int = 0,
    val isLoadingProgress: Boolean = false,
    val profileImageUri: Uri? = null
)

class ProfileViewModel(
    authRepository: AuthRepository,
    private val achievementsRepository: AchievementsRepository,
    private val dataStoreRepository: DataStoreRepository
) : BaseViewModel(authRepository) {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
        observeProfileImage()
    }

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

    private fun observeProfileImage() {
        withAuth { user ->
            dataStoreRepository.profilePictureURI(userId = user.id).collect { uri ->
                _uiState.update { it.copy(profileImageUri = uri) }
            }
        }
    }

    fun updateProfileImage(uri: Uri?) {
        withAuth { user ->
            uri?.let { dataStoreRepository.setProfilePictureURI(userId = user.id, uri = it) }
                ?: dataStoreRepository.removeProfilePictureURI(userId = user.id)
        }
    }
}