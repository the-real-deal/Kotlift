package com.therealdeal.kotlift.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therealdeal.kotlift.model.Stats
import com.therealdeal.kotlift.ui.composables.cards.MiniStatCard
import com.therealdeal.kotlift.ui.composables.cards.SessionCard
import com.therealdeal.kotlift.ui.composables.chart.WeeklyActivity
import com.therealdeal.kotlift.ui.composables.headers.SectionHeader
import com.therealdeal.kotlift.ui.theme.AppGreen
import com.therealdeal.kotlift.ui.theme.IconBlue
import com.therealdeal.kotlift.ui.theme.IconPurple
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = koinViewModel(),
    innerPadding: PaddingValues,
    scrollToAllSessions: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is StatsUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is StatsUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadStats() }) {
                        Text("Retry")
                    }
                }
            }
        }

        is StatsUiState.Success -> {
            StatsContent(
                stats = state.stats,
                innerPadding = innerPadding,
                scrollToAllSessions = scrollToAllSessions
            )
        }
    }
}

@Composable
private fun StatsContent(
    stats: Stats,
    innerPadding: PaddingValues,
    scrollToAllSessions: Boolean
) {
    val allSessionsBringIntoViewRequester = remember { BringIntoViewRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = innerPadding.calculateTopPadding())
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                "Your Progress",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Performance overview for this week",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniStatCard(
                    "Volume",
                    formatWeight(stats.totalWeightLifted),
                    Icons.Default.Scale,
                    AppGreen,
                    Modifier.weight(1f)
                )
                MiniStatCard(
                    "Sessions",
                    stats.totalSessions.toString(),
                    Icons.Default.FitnessCenter,
                    IconBlue,
                    Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniStatCard(
                    "Avg Time",
                    "${stats.avgSessionMinutes.roundToInt()} min",
                    Icons.Default.Timer,
                    IconPurple,
                    Modifier.weight(1f)
                )
                MiniStatCard(
                    "Top Workout",
                    stats.mostDoneWorkout?.let { "${it.count}x" } ?: "-",
                    Icons.AutoMirrored.Filled.TrendingUp,
                    AppGreen,
                    Modifier.weight(1f)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Weekly Activity",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            WeeklyActivity(
                data = stats.last7DaysActivity.map { (a, b) -> Pair(a, b * 60) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        if (stats.allSessions.isNotEmpty()) {
            Column(
                modifier = Modifier.bringIntoViewRequester(allSessionsBringIntoViewRequester)
            ) {
                SectionHeader(title = "All Sessions")
                stats.allSessions.forEach { session ->
                    SessionCard(session = session)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    LaunchedEffect(scrollToAllSessions, stats.allSessions) {
        if (scrollToAllSessions && stats.allSessions.isNotEmpty()) {
            allSessionsBringIntoViewRequester.bringIntoView()
        }
    }
}

private fun formatWeight(kg: Double): String {
    return if (kg >= 1000) "${"%.1f".format(kg / 1000)}k kg"
    else "${kg.roundToInt()} kg"
}