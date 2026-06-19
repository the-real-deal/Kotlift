package com.therealdeal.kotlift.ui.composables.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealdeal.kotlift.ui.theme.AppGreen
import com.therealdeal.kotlift.ui.theme.Gray

@Composable
fun WorkoutExerciseCard(
    exerciseName: String,
    targetMuscles: String,
    sets: List<String>,
    completedSets: List<Boolean>,
    onSetCheckedChange: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = exerciseName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Target: $targetMuscles",
                fontSize = 13.sp,
                color = AppGreen,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "SERIE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Gray, modifier = Modifier.weight(1f))
                Text(text = "DATO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Gray, modifier = Modifier.weight(2f), textAlign = TextAlign.Center)
                Text(text = "FATTO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Gray, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            }

            Spacer(modifier = Modifier.height(6.dp))

            sets.forEachIndexed { index, setInfo ->
                val isDone = completedSets.getOrElse(index) { false }

                WorkoutSetRow(
                    index = index + 1,
                    setInfo = setInfo,
                    isDone = isDone,
                    onCheckedChange = { onSetCheckedChange(index, it) }
                )

                if (index < sets.lastIndex) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutSetRow(
    index: Int,
    setInfo: String,
    isDone: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isDone) AppGreen.copy(alpha = 0.08f) else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$index",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = if (isDone) AppGreen else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = setInfo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(
                onClick = { onCheckedChange(!isDone) },
                modifier = Modifier.size(32.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isDone) AppGreen else Gray.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}