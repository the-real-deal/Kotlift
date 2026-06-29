package com.therealdeal.kotlift.ui.screens.activeWorkout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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
    navController: NavController,
    viewModel: ActiveWorkoutViewModel = koinViewModel(parameters = { parametersOf(workoutId) }),
    onNavigate: (ActiveWorkoutNavigation) -> Unit,
    innerPadding: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsStateWithLifecycle()
    val saveSessionState by viewModel.saveSessionState.collectAsStateWithLifecycle()

    val timerText = remember(elapsedSeconds) {
        val h = elapsedSeconds / 3600
        val m = (elapsedSeconds % 3600) / 60
        val s = elapsedSeconds % 60
        "%02d:%02d:%02d".format(h, m, s)
    }

    var showExitDialog by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }
    val setsMap = remember { mutableStateMapOf<String, List<SetData>>() }

    BackHandler { showExitDialog = true }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val selectedExerciseId by savedStateHandle
        ?.getStateFlow("selected_exercise_id", "")
        ?.collectAsStateWithLifecycle() ?: remember { mutableStateOf("") }

    LaunchedEffect(selectedExerciseId) {
        if (selectedExerciseId.isNotBlank()) {
            viewModel.addExercise(selectedExerciseId)
            savedStateHandle?.set("selected_exercise_id", "")
        }
    }

    LaunchedEffect(saveSessionState) {
        if (saveSessionState is SaveSessionState.Saved) {
            onNavigate(ActiveWorkoutNavigation.Back)
        }
    }

    val allSetsDone by remember {
        derivedStateOf {
            setsMap.values.isNotEmpty() && setsMap.values.all { sets -> sets.all { it.isDone } }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Abandon workout?") },
            text = { Text("Your progress will not be saved.") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    onNavigate(ActiveWorkoutNavigation.Back)
                }) {
                    Text("Exit", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Keep going")
                }
            }
        )
    }

    if (showFinishDialog) {
        if (!allSetsDone) {
            AlertDialog(
                onDismissRequest = { showFinishDialog = false },
                title = { Text("Incomplete workout") },
                text = { Text("You haven't completed all sets. Mark every set as done before finishing.") },
                confirmButton = {
                    TextButton(onClick = { showFinishDialog = false }) {
                        Text("Ok")
                    }
                }
            )
        } else {
            AlertDialog(
                onDismissRequest = { showFinishDialog = false },
                title = { Text("Finish workout?") },
                text = { Text("Your session will be saved.") },
                confirmButton = {
                    TextButton(onClick = {
                        showFinishDialog = false
                        viewModel.finishWorkout(setsMap)
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFinishDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

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

                LaunchedEffect(workout.exercises) {
                    workout.exercises.forEach { ex ->
                        if (!setsMap.containsKey(ex.routineExerciseId)) {
                            setsMap[ex.routineExerciseId] = List(ex.targetSets) {
                                SetData(
                                    targetReps = ex.targetReps,
                                    suggestedWeight = if (ex.targetWeight > 0.0)
                                        ex.targetWeight.toInt().toString() else ""
                                )
                            }
                        }
                    }
                    val activeIds = workout.exercises.map { it.routineExerciseId }.toSet()
                    setsMap.keys.retainAll(activeIds)
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

                    itemsIndexed(
                        workout.exercises,
                        key = { _, exercise -> exercise.routineExerciseId }
                    ) { _, exercise ->
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
                                },
                                onRemoveExercise = {
                                    viewModel.removeExercise(exercise.routineExerciseId)
                                }
                            )
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onNavigate(ActiveWorkoutNavigation.OpenExercisePicker) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add exercise")
                            }
                        }
                    }
                }

                ActiveWorkoutHeader(
                    timerText = timerText,
                    progressText = "Workout in progress",
                    onCloseClick = { showExitDialog = true }
                )

                if (saveSessionState is SaveSessionState.Saving) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    BottomFloatingButton(
                        text = "Finish",
                        onClick = { showFinishDialog = true },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Finish workout"
                            )
                        }
                    )
                }

                if (saveSessionState is SaveSessionState.Error) {
                    Snackbar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 100.dp)
                    ) {
                        Text((saveSessionState as SaveSessionState.Error).message)
                    }
                }
            }
        }
    }
}