package com.therealdeal.kotlift.ui.screens.profile

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ComponentActivity
import com.therealdeal.kotlift.model.Theme
import com.therealdeal.kotlift.ui.composables.commonComponents.AchievementsSection
import com.therealdeal.kotlift.ui.composables.headers.ProfileHeaderSection
import com.therealdeal.kotlift.ui.composables.settings.SettingsSection
import com.therealdeal.kotlift.ui.baseAuthentication.AuthenticatedScreen
import com.therealdeal.kotlift.ui.theme.ThemeViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun ProfileScreen(currentTheme: Theme, onThemeChange: (Theme) -> Unit) {
    AuthenticatedScreen<ProfileViewModel> { viewModel ->
        val uiState by viewModel.uiState.collectAsState()
        LaunchedEffect(Unit) { viewModel.loadProfileData() }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
            ) {
                ProfileHeaderSection(uiState.profile)

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    //GoalsCard()

                    Spacer(modifier = Modifier.height(20.dp))

                    AchievementsSection(achievements = uiState.achievements,
                        currentProgress = uiState.progress,
                        isProgressLoading = uiState.isLoadingProgress,
                        onDismiss = {
                            viewModel.resetProgress()
                        }) { achievement ->
                            viewModel.loadProgress(achievement)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingsSection(currentTheme){ theme ->
                        onThemeChange(theme)
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log Out", color = MaterialTheme.colorScheme.error)
                        }
                    }

                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }

    }
}
