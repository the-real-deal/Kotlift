package com.therealdeal.kotlift.ui.screens.activeWorkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therealdeal.kotlift.navigation.ActiveWorkoutNavigation
import com.therealdeal.kotlift.ui.composables.buttons.BottomFloatingButton
import com.therealdeal.kotlift.ui.composables.cards.SetData
import com.therealdeal.kotlift.ui.composables.cards.WorkoutExerciseCard
import com.therealdeal.kotlift.ui.composables.headers.ActiveWorkoutHeader
import com.therealdeal.kotlift.ui.theme.Gray
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ActiveWorkoutScreen(
    workoutId: String,
    viewModel: ActiveWorkoutViewModel = koinViewModel(parameters = { parametersOf(workoutId) }),
    onNavigate: (ActiveWorkoutNavigation) -> Unit,
    innerPadding: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val state = uiState) {
            is ActiveWorkoutUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ActiveWorkoutUiState.Error -> {
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

            is ActiveWorkoutUiState.Success -> {
                val workout = state.workout

                val setsMap = remember(workout.exercises) {
                    mutableStateMapOf<String, List<SetData>>().apply {
                        workout.exercises.forEach { ex ->
                            this[ex.routineExerciseId] = List(ex.targetSets) { index ->
                                SetData(
                                    targetReps = ex.targetReps,
                                    suggestedWeight = if (ex.targetWeight > 0.0)
                                        ex.targetWeight.toInt().toString()
                                    else ""
                                )
                            }
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 210.dp,
                        bottom = innerPadding.calculateBottomPadding() + 100.dp,
                        start = 4.dp,
                        end = 4.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                text = "Exercises",
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

                    itemsIndexed(workout.exercises) { _, exercise ->
                        val sets = setsMap[exercise.routineExerciseId] ?: emptyList()

                        Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                            WorkoutExerciseCard(
                                exerciseName = exercise.exercise.name
                                    .replaceFirstChar { it.uppercase() },
                                targetMuscles = exercise.exercise.targetMuscles
                                    .joinToString(", ")
                                    .replaceFirstChar { it.uppercase() },
                                gifUrl = exercise.exercise.gifUrl,
                                onHeaderClick = {
                                    onNavigate(ActiveWorkoutNavigation.ExerciseDetail(exercise.externalExerciseId))
                                },
                                sets = sets,
                                onSetChanged = { index, updated ->
                                    setsMap[exercise.routineExerciseId] =
                                        sets.toMutableList().apply { this[index] = updated }
                                },
                                onAddSet = {
                                    val last = sets.lastOrNull()
                                    setsMap[exercise.routineExerciseId] = sets + SetData(
                                        targetReps = last?.targetReps ?: exercise.targetReps,
                                        suggestedWeight = last?.weight?.ifBlank {
                                            last.suggestedWeight
                                        } ?: last?.suggestedWeight ?: ""
                                    )
                                },
                                onRemoveSet = {
                                    if (sets.size > 1) {
                                        setsMap[exercise.routineExerciseId] = sets.dropLast(1)
                                    }
                                }
                            )
                        }
                    }
                }

                ActiveWorkoutHeader(
                    timerText = "00:00:00",
                    progressText = "Workout in progress",
                    onCloseClick = { onNavigate(ActiveWorkoutNavigation.Back) }
                )

                BottomFloatingButton(
                    text = "Stop",
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Stop"
                        )
                    }
                )
            }
        }
    }
}