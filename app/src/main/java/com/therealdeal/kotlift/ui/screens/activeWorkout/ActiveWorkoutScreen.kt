package com.therealdeal.kotlift.ui.screens.activeWorkout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
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

    // setsMap fuori dal when per poterlo passare a finishWorkout
    val setsMap = remember { mutableStateMapOf<String, List<SetData>>() }

    // Intercetta back fisico
    BackHandler { showExitDialog = true }

    // Legge esercizio selezionato tornato da ExercisesScreen
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

    val allSetsDone = remember(setsMap) {
        setsMap.values.isNotEmpty() && setsMap.values.all { sets -> sets.all { it.isDone } }
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Finire il workout?") },
            text = {
                if (allSetsDone) {
                    Text("Il workout verrà salvato.")
                } else {
                    Text(
                        "Non hai completato tutte le serie. Completa tutti i set prima di finire.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFinishDialog = false
                        viewModel.finishWorkout(setsMap)
                    },
                    enabled = allSetsDone  // ← disabilitato se non tutti i set sono done
                ) {
                    Text("Salva")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }

    // Dialog: finisci e salva
    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Finire il workout?") },
            text = { Text("Il workout verrà salvato.") },
            confirmButton = {
                TextButton(onClick = {
                    showFinishDialog = false
                    viewModel.finishWorkout(setsMap)
                }) {
                    Text("Salva")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Annulla")
                }
            }
        )
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
                        Button(onClick = { viewModel.loadWorkoutDetail() }) { Text("Retry") }
                    }
                }
            }

            is ActiveWorkoutUiState.Success -> {
                val workout = state.workout

                // Sincronizza setsMap con gli esercizi correnti
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
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Aggiungi esercizio")
                            }
                        }
                    }
                }

                ActiveWorkoutHeader(
                    timerText = timerText,
                    progressText = "Workout in progress",
                    onCloseClick = { showExitDialog = true }  // ← dialog, non Back diretto
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
                        text = "Fine",
                        onClick = { showFinishDialog = true },  // ← dialog, non salva diretto
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp),
                        icon = {
                            Icon(imageVector = Icons.Default.Face, contentDescription = "Fine")
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