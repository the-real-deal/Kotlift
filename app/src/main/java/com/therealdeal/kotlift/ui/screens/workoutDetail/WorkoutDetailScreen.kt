package com.therealdeal.kotlift.ui.screens.workoutDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therealdeal.kotlift.navigation.WorkoutDetailNavigation
import com.therealdeal.kotlift.ui.composables.buttons.BottomFloatingButton
import com.therealdeal.kotlift.ui.composables.cards.ExerciseRowCard
import com.therealdeal.kotlift.ui.composables.headers.WorkoutDetailHeader
import com.therealdeal.kotlift.ui.theme.Gray
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun WorkoutDetailScreen(
    workoutId: String,
    viewModel: WorkoutDetailViewModel = koinViewModel(parameters = { parametersOf(workoutId) }),
    onNavigate: (WorkoutDetailNavigation) -> Unit,
    innerPadding: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val state = uiState) {
            is WorkoutDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is WorkoutDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadWorkoutDetail() }) {
                            Text("Retry")
                        }
                    }
                }
            }

            is WorkoutDetailUiState.Success -> {
                val workout = state.workout

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 210.dp,
                        bottom = innerPadding.calculateBottomPadding() + 80.dp,
                        start = 4.dp,
                        end = 4.dp
                    )
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Included exercises",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${workout.exercises.size} movements",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray
                            )
                        }
                    }

                    items(workout.exercises) { exerciseWithDetails ->
                        val totalSets = exerciseWithDetails.sets.size
                        val avgReps = if (exerciseWithDetails.sets.isEmpty()) 0
                        else exerciseWithDetails.sets.map { it.targetReps }.average().toInt()

                        val subtitle = "$totalSets Sets • $avgReps Reps"
                        ExerciseRowCard(
                            title = exerciseWithDetails.exercise.name.replaceFirstChar { it.uppercase() },
                            subtitle = subtitle,
                            gifUrl = exerciseWithDetails.exercise.gifUrl,
                            onClick = {
                                onNavigate(
                                    WorkoutDetailNavigation.ExerciseDetail(exerciseWithDetails.externalExerciseId)
                                )
                            }
                        )
                    }
                }

                WorkoutDetailHeader(
                    title = workout.name,
                    duration = "${workout.estimatedTimeMinutes ?: 0} min",
                    difficulty = workout.difficulty?.name ?: "",
                    createdAt = workout.createdAt.toString() ?: "",
                    onBackClick = { onNavigate(WorkoutDetailNavigation.Back) }
                )

                BottomFloatingButton(
                    text = "Start",
                    onClick = { onNavigate(WorkoutDetailNavigation.ActiveWorkout(workoutId)) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                    icon = {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start")
                    }
                )
            }
        }
    }
}