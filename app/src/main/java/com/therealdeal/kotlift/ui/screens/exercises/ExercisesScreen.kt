package com.therealdeal.kotlift.ui.screens.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therealdeal.kotlift.navigation.ExercisesNavigation
import com.therealdeal.kotlift.ui.composables.cards.ExerciseGridCard
import com.therealdeal.kotlift.ui.composables.commonComponents.ExerciseFilterSheet
import com.therealdeal.kotlift.ui.composables.headers.ExercisesHeader
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExercisesScreen(
    onNavigate: (ExercisesNavigation) -> Unit,
    innerPadding: PaddingValues,
    selectionMode: Boolean = false  // ← nuovo parametro
) {
    val viewModel: ExercisesViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val gridState = rememberLazyGridState()
    val localDensity = LocalDensity.current
    var headerHeightDp by remember { mutableStateOf(0.dp) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val shouldLoadMore by remember {
        derivedStateOf {
            val visibleItems = gridState.layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@derivedStateOf false
            val lastVisible = visibleItems.last().index
            val totalItems = gridState.layoutInfo.totalItemsCount
            lastVisible >= totalItems - 3 && uiState.hasNextPage && !uiState.isLoadingMore
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadNextPage()
    }

    if (showFilterSheet) {
        ExerciseFilterSheet(
            muscles = uiState.muscles,
            bodyParts = uiState.bodyParts,
            equipments = uiState.equipments,
            currentFilters = uiState.filters,
            onApply = { viewModel.applyFilters(it) },
            onDismiss = { showFilterSheet = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null && uiState.allExercises.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(uiState.error!!)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadExercises() }) { Text("Retry") }
                    }
                }
            }

            else -> {
                LazyVerticalGrid(
                    state = gridState,
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
                    items(uiState.filteredExercises, key = { it.id }) { exercise ->
                        ExerciseGridCard(
                            name = exercise.name.replaceFirstChar { it.uppercase() },
                            category = exercise.equipment.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "",
                            target = exercise.targetMuscles.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "",
                            imageUrl = exercise.gifUrl,
                            selectionMode = selectionMode,
                            onClick = {
                                if (selectionMode) {
                                    onNavigate(ExercisesNavigation.ExerciseSelected(exercise.id, exercise.name))
                                } else {
                                    onNavigate(ExercisesNavigation.ExerciseDetail(exercise.id))
                                }
                            }
                        )
                    }

                    if (uiState.isLoadingMore) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                    }
                }
            }
        }

        ExercisesHeader(
            searchText = uiState.searchQuery,
            onSearchChange = { viewModel.onSearchQueryChange(it) },
            onBackClick = { onNavigate(ExercisesNavigation.Back) },
            onHeightMeasured = { height -> headerHeightDp = with(localDensity) { height.toDp() } },
            filterBadgeCount = uiState.filters.activeCount,
            onFilterClick = { showFilterSheet = true }
        )
    }
}