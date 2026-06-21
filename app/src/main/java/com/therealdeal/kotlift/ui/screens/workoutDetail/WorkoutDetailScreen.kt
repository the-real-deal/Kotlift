package com.therealdeal.kotlift.ui.screens.workoutDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealdeal.kotlift.ui.composables.buttons.BottomFloatingButton
import com.therealdeal.kotlift.ui.composables.cards.ModernExerciseCard
import com.therealdeal.kotlift.ui.composables.headers.WorkoutDetailHeader
import com.therealdeal.kotlift.ui.theme.Gray
import com.therealdeal.kotlift.navigation.WorkoutDetailNavigation

@Composable
fun WorkoutDetailScreen(
    onNavigate: (WorkoutDetailNavigation) -> Unit,
    innerPadding: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val state = WorkoutDetailUiState.Success("culo")
        when (state) {
//            is WorkoutDetailUiState.Loading -> {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator()
//                }
//            }
//
//            is WorkoutDetailUiState.Error -> {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Button(onClick = { }) {
//                            Text("Riprova")
//                        }
//                    }
//                }
//            }

            is WorkoutDetailUiState.Success -> {

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
                                text = "Esercizi inclusi",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "5 movimenti",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray
                            )
                        }
                    }

                    items(5) { index ->
                        val subtitleText =
                                "5 Serie • culo Reps • chiappe kg"

                        ModernExerciseCard(
                            title = "sedere", // Riferimento corretto all'oggetto ExerciseWithDetails (.name)
                            subtitle = subtitleText,
                            onClick = { onNavigate(WorkoutDetailNavigation.ExerciseDetail) }
                        )
                    }
                }

                // Aggiornato con calorie reali dal DB e durata stimata corretta
                WorkoutDetailHeader(
                    title = "workout.title",
                    duration = "30 min",
                    calories = "$100 kcal", // <-- REALI DAL DB!
                    level =  "Intermediate",
                    onBackClick = { onNavigate(WorkoutDetailNavigation.Back) }
                )

                BottomFloatingButton(
                    text = "Start",
                    onClick = { onNavigate(WorkoutDetailNavigation.ActiveWorkout) },
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