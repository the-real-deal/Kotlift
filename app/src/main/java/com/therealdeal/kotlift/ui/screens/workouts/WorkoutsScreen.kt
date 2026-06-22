package com.therealdeal.kotlift.ui.screens.workouts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    Scaffold(
        modifier = Modifier.padding(
            top = innerPadding.calculateTopPadding()
        ),
        topBar = {

            WorkoutsTopBar(
                searchText = "",
                onSearchTextChange = {},
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//            FilterRow(
//                categories = categories,
//                selectedCategory = currentCategory,
//                onCategorySelected = {  },
//                modifier = Modifier.fillMaxWidth()
//            )

            when (val state = uiState) {
                is WorkoutsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is WorkoutsUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
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
                    if (state.workouts.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No workouts created yet")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.workouts) { workout ->
                                WorkoutCard(workout = workout, onClick = {onNavigate(WorkoutsNavigation.WorkoutDetail(workout.id))})
                            }
                        }
                    }
                }
            }
        }
    }
}