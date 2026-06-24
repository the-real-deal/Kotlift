package com.therealdeal.kotlift.ui.screens.exerciseDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therealdeal.kotlift.navigation.ExerciseDetailNavigation
import com.therealdeal.kotlift.ui.composables.cards.ExerciseDetailTags
import com.therealdeal.kotlift.ui.composables.cards.ExerciseMuscleCardRow
import com.therealdeal.kotlift.ui.composables.headers.ExerciseDetailHeader
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ExerciseDetailScreen(
    exerciseId: String,
    viewModel: ExerciseDetailViewModel = koinViewModel(parameters = { parametersOf(exerciseId) }),
    onNavigate: (ExerciseDetailNavigation) -> Unit,
    innerPadding: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val state = uiState) {
            is ExerciseDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ExerciseDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadExerciseDetail() }) {
                            Text("Retry")
                        }
                    }
                }
            }

            is ExerciseDetailUiState.Success -> {
                val exercise = state.exercise

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 300.dp,
                        bottom = innerPadding.calculateBottomPadding() + 16.dp,
                        start = 4.dp,
                        end = 4.dp
                    )
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            ExerciseDetailTags(
                                bodyPart = exercise.bodyParts.firstOrNull() ?: "",
                                equipment = exercise.equipments.firstOrNull() ?: ""
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            ExerciseMuscleCardRow(
                                primaryTarget = exercise.targetMuscles.joinToString(", ")
                                    .replaceFirstChar { it.uppercase() },
                                secondaryTarget = exercise.secondaryMuscles.joinToString(", ")
                                    .replaceFirstChar { it.uppercase() }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            ExerciseInstructionsList(
                                steps = exercise.instructions.map { instruction ->
                                    instruction.replace(Regex("^Step:\\d+\\s*"), "")
                                }
                            )
                        }
                    }
                }

                ExerciseDetailHeader(
                    title = exercise.name.replaceFirstChar { it.uppercase() },
                    gifUrl = exercise.gifUrl,
                    onBackClick = { onNavigate(ExerciseDetailNavigation.Back) }
                )
            }
        }
    }
}

@Composable
fun ExerciseInstructionsList(
    steps: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Instructions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        steps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "${index + 1}.",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    modifier = Modifier.width(24.dp)
                )
                Text(
                    text = step,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}