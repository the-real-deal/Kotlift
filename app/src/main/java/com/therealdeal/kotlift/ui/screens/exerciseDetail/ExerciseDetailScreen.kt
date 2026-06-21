package com.therealdeal.kotlift.ui.screens.exerciseDetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealdeal.kotlift.ui.composables.cards.ExerciseDetailTags
import com.therealdeal.kotlift.ui.composables.cards.ExerciseMuscleCardRow
import com.therealdeal.kotlift.ui.composables.cards.ExerciseNotesCard
import com.therealdeal.kotlift.ui.composables.headers.ExerciseDetailHeader
import com.therealdeal.kotlift.navigation.ExerciseDetailNavigation

@Composable
fun ExerciseDetailScreen(
    onNavigate: (ExerciseDetailNavigation) -> Unit,
    innerPadding: PaddingValues
) {
    val scrollState = rememberScrollState()

    val steps = listOf(
        "Mettiti in posizione di plank con le mani leggermente più larghe delle spalle.",
        "Mantieni il corpo in linea retta dai piedi alla testa, contraendo core e glutei.",
        "Abbassa il corpo piegando i gomiti finché il petto non sfiora il pavimento.",
        "Spingi via il pavimento per tornare alla posizione di partenza estendendo completamente le braccia."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(scrollState)
    ) {
        ExerciseDetailHeader(
            title = "Piegamenti (Push-ups)",
            onBackClick = { onNavigate(ExerciseDetailNavigation.Back) }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ExerciseDetailTags(
                difficulty = "Principiante",
                category = "Corpo Libero"
            )

            Spacer(modifier = Modifier.height(24.dp))

            ExerciseMuscleCardRow(
                primaryTarget = "Petto",
                secondaryTarget = "Tricipiti, Spalle"
            )

            Spacer(modifier = Modifier.height(24.dp))

            ExerciseInstructionsList(steps = steps)

            Spacer(modifier = Modifier.height(24.dp))

            ExerciseNotesCard(
                title = "Errore comune",
                description = "Evita di far cadere il bacino verso il basso o di alzare troppo i glutei. Mantieni il core solido."
            )
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
            text = "Istruzioni",
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