package com.therealdeal.kotlift.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therealdeal.kotlift.model.Profile
import com.therealdeal.kotlift.model.Session
import com.therealdeal.kotlift.navigation.HomeNavigation
import com.therealdeal.kotlift.ui.composables.cards.CreateWorkoutCard
import com.therealdeal.kotlift.ui.composables.cards.SmallActionCard
import com.therealdeal.kotlift.ui.composables.cards.StatCard
import com.therealdeal.kotlift.ui.composables.headers.HomeHeader
import com.therealdeal.kotlift.ui.composables.headers.SectionHeader
import com.therealdeal.kotlift.ui.theme.AppGreen
import com.therealdeal.kotlift.ui.theme.IconBlue
import com.therealdeal.kotlift.ui.theme.IconPurple
import com.therealdeal.kotlift.ui.theme.IconRed
import com.therealdeal.kotlift.ui.theme.IconYellow
import org.koin.androidx.compose.koinViewModel
import com.therealdeal.kotlift.ui.composables.cards.SessionCard

@Composable
fun HomeScreen(
    onNavigate: (HomeNavigation) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is HomeUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is HomeUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadHomeData() }) {
                        Text("Retry")
                    }
                }
            }
        }

        is HomeUiState.Success -> {
            HomeContent(
                profile = state.profile,
                latestSessions = state.latestSessions,
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
private fun HomeContent(
    profile: Profile,
    latestSessions: List<Session>,
    onNavigate: (HomeNavigation) -> Unit
) {
    val recentSessions = latestSessions.take(3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        HomeHeader(profile.username)

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-30).dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Day Streak", profile.dayStreak.toString(), Icons.Default.LocalFireDepartment, IconRed, Modifier.weight(1f))
                StatCard("Sessions", profile.totalSessions.toString(), Icons.Default.FitnessCenter, AppGreen, Modifier.weight(1f))
                StatCard("Trophy", profile.unlockedAchievementsCount.toString(), Icons.Default.EmojiEvents, IconYellow, Modifier.weight(1f))
            }

            CreateWorkoutCard("Create a new workout?", "Start your workout now") { onNavigate(HomeNavigation.CreateWorkout) }

            if (recentSessions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Recent Sessions",
                    onSeeAllClick = { onNavigate(HomeNavigation.Stats(scrollToAllSessions = true)) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                recentSessions.forEach { session ->
                    SessionCard(session = session)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SmallActionCard("Exercise Library", Icons.AutoMirrored.Filled.MenuBook, IconBlue, { onNavigate(HomeNavigation.Exercises) }, Modifier.weight(1f))
                SmallActionCard("Track Progress", Icons.AutoMirrored.Filled.TrendingUp, IconPurple, { onNavigate(HomeNavigation.Stats()) }, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}