package com.therealdeal.kotlift.ui.screens.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.therealdeal.kotlift.ui.composables.cards.ExerciseGridCard
import com.therealdeal.kotlift.navigation.ExercisesNavigation
import com.therealdeal.kotlift.ui.composables.headers.ExercisesHeader

@Composable
fun ExercisesScreen(
    onNavigate: (ExercisesNavigation) -> Unit,
    innerPadding: PaddingValues
) {
    var searchText by remember { mutableStateOf("") }
    val muscleGroups = listOf("Tutti", "Petto", "Dorso", "Gambe", "Spalle", "Braccia", "Core")
    var selectedMuscle by remember { mutableStateOf("Tutti") }

    val localDensity = LocalDensity.current
    var headerHeightDp by remember { mutableStateOf(0.dp) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = headerHeightDp + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(12) { index ->
                ExerciseGridCard(
                    name = listOf(
                        "Piegamenti",
                        "Trazioni",
                        "Squat",
                        "Affondi",
                        "Plank",
                        "Dip"
                    )[index % 6],
                    category = if (index % 2 == 0) "Corpo Libero" else "Con Pesi",
                    target = "Target: Petto",
                    imageUrl = "https://i.makeagif.com/media/7-17-2021/63CZzt.gif",
                    onClick = {onNavigate(ExercisesNavigation.ExerciseDetail("0"))}
                )
            }
        }

        ExercisesHeader(
            searchText = searchText,
            onSearchChange = { searchText = it },
            muscleGroups = muscleGroups,
            selectedMuscle = selectedMuscle,
            onMuscleSelect = { selectedMuscle = it },
            onBackClick = { onNavigate(ExercisesNavigation.Back) },
            onHeightMeasured = { height -> headerHeightDp = with(localDensity) { height.toDp() } }
        )
    }
}