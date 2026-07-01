package com.therealdeal.kotlift.ui.screens.workouts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therealdeal.kotlift.navigation.WorkoutsNavigation
import com.therealdeal.kotlift.ui.composables.cards.WorkoutCard
import com.therealdeal.kotlift.ui.composables.headers.WorkoutsTopBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsScreen(
    viewModel: WorkoutsViewModel = koinViewModel(),
    onNavigate: (WorkoutsNavigation) -> Unit,
    innerPadding: PaddingValues,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var workoutToDelete by remember { mutableStateOf<String?>(null) }

    if (workoutToDelete != null) {
        AlertDialog(
            onDismissRequest = { workoutToDelete = null },
            title = { Text("Delete workout?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteWorkout(workoutToDelete!!)
                    workoutToDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { workoutToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
        topBar = {
            WorkoutsTopBar(
                searchText = searchQuery,
                onSearchTextChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            when (val state = uiState) {
                is WorkoutsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is WorkoutsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = state.message)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.loadWorkouts() }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                is WorkoutsUiState.Success -> {
                    PullToRefreshBox(
                        isRefreshing = state.isReloading,
                        onRefresh = viewModel::reload,
                    ) {
                        if (state.workouts.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    if (searchQuery.isBlank()) "No workouts created yet"
                                    else "No results for \"$searchQuery\""
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.workouts, key = { it.id }) { workout ->
                                    WorkoutCard(
                                        workout = workout,
                                        onClick = { onNavigate(WorkoutsNavigation.WorkoutDetail(workout.id)) },
                                        onDeleteClick = { workoutToDelete = workout.id }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}