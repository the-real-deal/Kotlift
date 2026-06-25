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
import com.therealdeal.kotlift.ui.composables.headers.ActiveWorkoutHeader
import com.therealdeal.kotlift.ui.composables.cards.SetData
import com.therealdeal.kotlift.ui.composables.cards.WorkoutExerciseCard
import com.therealdeal.kotlift.ui.composables.buttons.BottomFloatingButton
import com.therealdeal.kotlift.ui.theme.Gray
import com.therealdeal.kotlift.navigation.ActiveWorkoutNavigation
import com.therealdeal.kotlift.navigation.WorkoutDetailNavigation
import com.therealdeal.kotlift.ui.screens.workoutDetail.WorkoutDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

data class ExerciseSetTemplate(
    val targetReps: Int,
    val suggestedWeight: String = ""
)

data class ExerciseData(
    val name: String,
    val targetMuscles: String,
    val sets: List<ExerciseSetTemplate>
)

@Composable
fun ActiveWorkoutScreen(
    workoutId: String,
    viewModel: WorkoutDetailViewModel = koinViewModel(parameters = { parametersOf(workoutId) }),
    onNavigate: (ActiveWorkoutNavigation) -> Unit,
    innerPadding: PaddingValues
) {
    val exercises = remember {
        listOf(
            ExerciseData(
                name = "Bench Press",
                targetMuscles = "Chest, Triceps",
                sets = listOf(
                    ExerciseSetTemplate(targetReps = 8,  suggestedWeight = "80"),
                    ExerciseSetTemplate(targetReps = 8,  suggestedWeight = "80"),
                    ExerciseSetTemplate(targetReps = 6,  suggestedWeight = "90"),
                    ExerciseSetTemplate(targetReps = 6,  suggestedWeight = "90")
                )
            ),
            ExerciseData(
                name = "Pull-ups",
                targetMuscles = "Back, Biceps",
                sets = listOf(
                    ExerciseSetTemplate(targetReps = 10),
                    ExerciseSetTemplate(targetReps = 8),
                    ExerciseSetTemplate(targetReps = 8)
                )
            ),
            ExerciseData(
                name = "Barbell Squat",
                targetMuscles = "Quads, Glutes",
                sets = listOf(
                    ExerciseSetTemplate(targetReps = 6, suggestedWeight = "100"),
                    ExerciseSetTemplate(targetReps = 6, suggestedWeight = "100"),
                    ExerciseSetTemplate(targetReps = 4, suggestedWeight = "110")
                )
            ),
            ExerciseData(
                name = "Military Press",
                targetMuscles = "Shoulders, Triceps",
                sets = listOf(
                    ExerciseSetTemplate(targetReps = 8, suggestedWeight = "50"),
                    ExerciseSetTemplate(targetReps = 6, suggestedWeight = "55")
                )
            )
        )
    }

    val setsMap = remember {
        mutableStateMapOf<String, List<SetData>>().apply {
            exercises.forEach { ex ->
                this[ex.name] = ex.sets.map { template ->
                    SetData(
                        targetReps      = template.targetReps,
                        suggestedWeight = template.suggestedWeight
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
                        text = "${exercises.size} movements",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray
                    )
                }
            }

            itemsIndexed(exercises) { _, exercise ->
                val sets = setsMap[exercise.name] ?: emptyList()
                val lastTemplate = exercise.sets.lastOrNull()

                Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                    WorkoutExerciseCard(
                        exerciseName = exercise.name,
                        targetMuscles = exercise.targetMuscles,
                        sets = sets,
                        onSetChanged = { index, updated ->
                            setsMap[exercise.name] = sets.toMutableList().apply { this[index] = updated }
                        },
                        onAddSet = {
                            val newSet = SetData(
                                targetReps      = lastTemplate?.targetReps ?: sets.lastOrNull()?.targetReps ?: 8,
                                suggestedWeight = sets.lastOrNull()?.weight?.ifBlank { sets.lastOrNull()?.suggestedWeight ?: "" } ?: ""
                            )
                            setsMap[exercise.name] = sets + newSet
                        },
                        onRemoveSet = {
                            if (sets.size > 1) setsMap[exercise.name] = sets.dropLast(1)
                        }
                    )
                }
            }
        }

        ActiveWorkoutHeader(
            timerText = "00:14:25",
            progressText = "Workout in progress",
            onCloseClick = { onNavigate(ActiveWorkoutNavigation.Back) }
        )

        BottomFloatingButton(
            text = "Stop",
            onClick = { },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "Stop"
                )
            }
        )
    }
}