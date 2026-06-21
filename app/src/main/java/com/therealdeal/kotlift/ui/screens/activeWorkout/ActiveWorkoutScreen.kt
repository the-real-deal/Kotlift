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
import com.therealdeal.kotlift.ui.composables.cards.WorkoutExerciseCard
import com.therealdeal.kotlift.ui.composables.buttons.BottomFloatingButton
import com.therealdeal.kotlift.ui.theme.Gray
import com.therealdeal.kotlift.navigation.ActiveWorkoutNavigation

@Composable
fun ActiveWorkoutScreen(
    onNavigate: (ActiveWorkoutNavigation) -> Unit,
    innerPadding: PaddingValues
) {

    val exercises = remember {
        listOf(
            ExerciseData("Panca Piana", "Petto, Tricipiti", listOf("100 kg x 8", "100 kg x 8", "105 kg x 6", "105 kg x 6")),
            ExerciseData("Trazioni alla Sbarra", "Dorso, Bicipiti", listOf("Bodyweight x 10", "Bodyweight x 8", "Bodyweight x 8")),
            ExerciseData("Squat con Bilanciere", "Quadricipiti, Glutei", listOf("120 kg x 6", "120 kg x 6", "130 kg x 4")),
            ExerciseData("Military Press", "Spalle, Tricipiti", listOf("50 kg x 8", "55 kg x 6"))
        )
    }

    val completedSetsMap = remember { mutableStateMapOf<String, List<Boolean>>() }

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
                        text = "Esercizi in corso",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${exercises.size} movimenti",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray
                    )
                }
            }

            itemsIndexed(exercises) { _, exercise ->
                val currentState = completedSetsMap[exercise.name] ?: List(exercise.sets.size) { false }

                Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                    WorkoutExerciseCard(
                        exerciseName = exercise.name,
                        targetMuscles = exercise.targetMuscles,
                        sets = exercise.sets,
                        completedSets = currentState,
                        onSetCheckedChange = { setIndex, isChecked ->
                            val updatedList = currentState.toMutableList().apply { this[setIndex] = isChecked }
                            completedSetsMap[exercise.name] = updatedList
                        }
                    )
                }
            }
        }

        ActiveWorkoutHeader(
            timerText = "00:14:25",
            progressText = "Allenamento avviato",
            onCloseClick = { onNavigate(ActiveWorkoutNavigation.Back) }
        )

        BottomFloatingButton(
            text = "Stop",
            onClick = { },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            {Icon(
                imageVector = Icons.Default.Face,
                contentDescription = "Stop"
            )}
        )
    }
}

data class ExerciseData(
    val name: String,
    val targetMuscles: String,
    val sets: List<String>
)