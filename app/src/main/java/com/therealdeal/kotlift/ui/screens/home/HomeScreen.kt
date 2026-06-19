package com.therealdeal.kotlift.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.therealdeal.kotlift.ui.composables.cards.QuickStartCard
import com.therealdeal.kotlift.ui.composables.cards.SmallActionCard
import com.therealdeal.kotlift.ui.composables.cards.StatCard
import com.therealdeal.kotlift.ui.composables.cards.WorkoutCard
import com.therealdeal.kotlift.ui.composables.headers.HomeHeader
import com.therealdeal.kotlift.ui.composables.headers.SectionHeader
import com.therealdeal.kotlift.ui.theme.AppGreen
import com.therealdeal.kotlift.ui.theme.IconPurple
import com.therealdeal.kotlift.ui.theme.IconRed
import com.therealdeal.kotlift.ui.theme.IconYellow
import com.therealdeal.kotlift.ui.theme.IconBlue
import com.therealdeal.kotlift.navigation.HomeNavigation

@Composable
fun HomeScreen(onNavigate: (HomeNavigation) -> Unit, innerPadding : PaddingValues){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        HomeHeader()

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-30).dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Day Streak", "0", Icons.Default.Face, IconRed, Modifier.weight(1f))
                StatCard("Workouts", "0", Icons.Default.Face, AppGreen, Modifier.weight(1f))
                StatCard("Records", "0", Icons.Default.Face, IconYellow, Modifier.weight(1f))
            }

            QuickStartCard("Ready to Train?", "Start your workout now", { onNavigate(HomeNavigation.CreateWorkout) })

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("Recommended for You")
            WorkoutCard(
                onClick = { onNavigate(HomeNavigation.WorkoutDetail) }
                )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SmallActionCard("Exercise Library", Icons.Default.Face, IconBlue,{onNavigate(HomeNavigation.Exercises)}, Modifier.weight(1f))
                SmallActionCard("Track Progress", Icons.Default.Face, IconPurple,{onNavigate(HomeNavigation.Stats)}, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}




