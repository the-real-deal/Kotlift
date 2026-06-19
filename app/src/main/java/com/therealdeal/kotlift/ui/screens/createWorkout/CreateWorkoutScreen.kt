package com.therealdeal.kotlift.ui.screens.createWorkout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.therealdeal.kotlift.navigation.CreateNavigation
import java.util.UUID


enum class DifficultyLevel {
    BEGINNER, INTERMEDIATE, ADVANCED
}

data class ExerciseItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val sets: Int = 3,
    val repetitions: Int = 10,
    val restSeconds: Int = 60
)

/**
 * UI State che rappresenta l'intera schermata di creazione del workout.
 * Implementato come data class per facilitare l'unidirectional data flow (UDF).
 */
data class CreateWorkoutUiState(
    val name: String = "",
    val description: String = "",
    val difficulty: DifficultyLevel = DifficultyLevel.BEGINNER,
    val exercises: List<ExerciseItem> = emptyList(),
    val isNameValid: Boolean = true
)

val CreateWorkoutUiStateSaver = Saver<CreateWorkoutUiState, Map<String, Any>>(
    save = { state ->
        mapOf(
            "name" to state.name,
            "description" to state.description,
            "difficulty" to state.difficulty.name,
            "isNameValid" to state.isNameValid
        )
    },
    restore = { map ->
        CreateWorkoutUiState(
            name = map["name"] as String,
            description = map["description"] as String,
            difficulty = DifficultyLevel.valueOf(map["difficulty"] as String),
            isNameValid = map["isNameValid"] as Boolean,
            exercises = emptyList()
        )
    }
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    onNavigate: (CreateNavigation) -> Unit,
    innerPadding: PaddingValues
) {
    var uiState by rememberSaveable(stateSaver = CreateWorkoutUiStateSaver) {
        mutableStateOf(CreateWorkoutUiState())
    }

    var exerciseList by rememberSaveable { mutableStateOf(listOf<ExerciseItem>()) }
    val currentUiState = uiState.copy(exercises = exerciseList)

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            WorkoutHeader(
                scrollBehavior = scrollBehavior,
                onBackClick = { onNavigate(CreateNavigation.Back) }
            )
        },
        bottomBar = {
            SaveWorkoutButton(
                onClick = {
                    val isValid = currentUiState.name.isNotBlank()
                    uiState = uiState.copy(isNameValid = isValid)
                    if (isValid) {
                        onNavigate(CreateNavigation.Back)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                WorkoutNameField(
                    name = currentUiState.name,
                    isError = !currentUiState.isNameValid,
                    onNameChange = {
                        uiState = uiState.copy(name = it, isNameValid = it.isNotBlank())
                    }
                )
            }

            item {
                OutlinedTextField(
                    value = currentUiState.description,
                    onValueChange = { uiState = uiState.copy(description = it) },
                    label = { Text("Descrizione (opzionale)") },
                    placeholder = { Text("Es. Focus sull'ipertrofia della parte superiore") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )
            }

            item {
                DifficultySelector(
                    selectedDifficulty = currentUiState.difficulty,
                    onDifficultySelected = { uiState = uiState.copy(difficulty = it) }
                )
            }

            item {
                Text(
                    text = "Esercizi",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (currentUiState.exercises.isEmpty()) {
                item {
                    EmptyExercisesState()
                }
            } else {
                items(
                    items = currentUiState.exercises,
                    key = { it.id }
                ) { exercise ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
                    ) {
                        ExerciseCard(
                            exercise = exercise,
                            onExerciseChange = { updated ->
                                exerciseList = exerciseList.map {
                                    if (it.id == updated.id) updated else it
                                }
                            },
                            onDeleteClick = {
                                exerciseList = exerciseList.filter { it.id != exercise.id }
                            }
                        )
                    }
                }
            }

            item {
                AddExerciseButton(
                    onClick = {
                        exerciseList = exerciseList + ExerciseItem()
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutHeader(
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LargeTopAppBar(
        title = {
            Text(
                text = "Crea Workout",
                style = MaterialTheme.typography.headlineLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Torna indietro"
                )
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
fun WorkoutNameField(
    name: String,
    isError: Boolean,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nome Workout *") },
            placeholder = { Text("Es. Full Body Spinta") },
            isError = isError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        if (isError) {
            Text(
                text = "Il nome del workout è obbligatorio",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun DifficultySelector(
    selectedDifficulty: DifficultyLevel,
    onDifficultySelected: (DifficultyLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Difficoltà",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DifficultyLevel.values().forEach { level ->
                val isSelected = level == selectedDifficulty

                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    label = "bgColorAnim"
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "contentColorAnim"
                )
                val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .clickable { onDifficultySelected(level) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (level) {
                            DifficultyLevel.BEGINNER -> "Beginner"
                            DifficultyLevel.INTERMEDIATE -> "Intermediate"
                            DifficultyLevel.ADVANCED -> "Advanced"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = contentColor
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: ExerciseItem,
    onExerciseChange: (ExerciseItem) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = exercise.name,
                    onValueChange = { onExerciseChange(exercise.copy(name = it)) },
                    label = { Text("Nome Esercizio") },
                    placeholder = { Text("Es. Panca Piana") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Elimina esercizio",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = exercise.sets.toString().takeIf { it != "0" } ?: "",
                    onValueChange = { value ->
                        val parsed = value.toIntOrNull() ?: 0
                        onExerciseChange(exercise.copy(sets = parsed))
                    },
                    label = { Text("Serie") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = exercise.repetitions.toString().takeIf { it != "0" } ?: "",
                    onValueChange = { value ->
                        val parsed = value.toIntOrNull() ?: 0
                        onExerciseChange(exercise.copy(repetitions = parsed))
                    },
                    label = { Text("Reps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = exercise.restSeconds.toString().takeIf { it != "0" } ?: "",
                    onValueChange = { value ->
                        val parsed = value.toIntOrNull() ?: 0
                        onExerciseChange(exercise.copy(restSeconds = parsed))
                    },
                    label = { Text("Rec. (s)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    singleLine = true,
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}

@Composable
fun AddExerciseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        border = ButtonDefaults.outlinedButtonColors().contentColor.let {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Aggiungi esercizio",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SaveWorkoutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Salva Workout",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun EmptyExercisesState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Face,
            contentDescription = null,
            modifier = Modifier
                .height(64.dp)
                .width(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nessun esercizio inserito",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Inizia ad aggiungere esercizi per comporre la tua scheda.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}
