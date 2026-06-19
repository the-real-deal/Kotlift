package com.therealdeal.kotlift.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealdeal.kotlift.ui.composables.cards.MiniStatCard
import com.therealdeal.kotlift.ui.theme.AppGreen
import com.therealdeal.kotlift.ui.theme.IconBlue
import com.therealdeal.kotlift.ui.theme.IconPurple
import com.therealdeal.kotlift.ui.theme.IconRed
import com.therealdeal.kotlift.ui.composables.chart.WeeklyActivity
import com.therealdeal.kotlift.navigation.StatsNavigation

@Composable
fun StatsScreen(onNavigate: (StatsNavigation) -> Unit, innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = innerPadding.calculateTopPadding()
            ).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 16.dp)) {
            Text("Your Progress", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text("Performance overview for this week", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniStatCard("Volume", "42.5k kg", Icons.AutoMirrored.Filled.List, AppGreen, Modifier.weight(1f))
                MiniStatCard("Calories", "12.400", Icons.Default.Face, IconRed, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniStatCard("Workouts", "12", Icons.Default.Face, IconBlue, Modifier.weight(1f))
                MiniStatCard("Avg Time", "54 min", Icons.AutoMirrored.Filled.KeyboardArrowLeft, IconPurple, Modifier.weight(1f))
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Weekly Activity",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            WeeklyActivity(modifier = Modifier.fillMaxSize().padding(bottom = 16.dp))
        }
    }
}