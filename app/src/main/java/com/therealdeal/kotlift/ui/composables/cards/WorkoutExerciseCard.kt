package com.therealdeal.kotlift.ui.composables.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealdeal.kotlift.ui.theme.AppGreen
import com.therealdeal.kotlift.ui.theme.Gray
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

data class SetData(
    val weight: String = "",
    val reps: String = "",
    val targetReps: Int,
    val suggestedWeight: String = "",
    val isDone: Boolean = false
)

@Composable
fun WorkoutExerciseCard(
    exerciseName: String,
    targetMuscles: String,
    gifUrl: String?,
    onHeaderClick: () -> Unit,
    sets: List<SetData>,
    onSetChanged: (index: Int, updated: SetData) -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: () -> Unit,
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

            // Header cliccabile con gif + nome + muscoli
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onHeaderClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = gifUrl,
                    contentDescription = exerciseName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                )
                Column {
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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "SET",  fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Gray, modifier = Modifier.weight(0.7f))
                Text(text = "KG",   fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Gray, modifier = Modifier.weight(1.3f), textAlign = TextAlign.Center)
                Text(text = "REPS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Gray, modifier = Modifier.weight(1.3f), textAlign = TextAlign.Center)
                Text(text = "DONE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Gray, modifier = Modifier.weight(1f),   textAlign = TextAlign.End)
            }

            Spacer(modifier = Modifier.height(6.dp))

            sets.forEachIndexed { index, set ->
                WorkoutSetRow(
                    index = index + 1,
                    set = set,
                    onWeightChange = { onSetChanged(index, set.copy(weight = it)) },
                    onRepsChange   = { onSetChanged(index, set.copy(reps = it)) },
                    onDoneToggle   = {
                        val resolvedWeight = set.weight.ifBlank { set.suggestedWeight }
                        val resolvedReps   = set.reps.ifBlank { set.targetReps.toString() }
                        onSetChanged(index, set.copy(
                            isDone = !set.isDone,
                            weight = if (!set.isDone) resolvedWeight else set.weight,
                            reps   = if (!set.isDone) resolvedReps   else set.reps
                        ))
                    }
                )
                if (index < sets.lastIndex) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRemoveSet,
                    enabled = sets.size > 1,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Gray,
                        disabledContentColor = Gray.copy(alpha = 0.3f)
                    )
                ) {
                    Text(text = "− Remove set", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onAddSet,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppGreen.copy(alpha = 0.15f),
                        contentColor = AppGreen
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add set", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun WorkoutSetRow(
    index: Int,
    set: SetData,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onDoneToggle: () -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val activeStyle = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = if (set.isDone) AppGreen else textColor,
        textAlign = TextAlign.Center
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (set.isDone) AppGreen.copy(alpha = 0.08f) else Color.Transparent,
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
            color = if (set.isDone) AppGreen else textColor,
            modifier = Modifier.weight(0.7f)
        )

        SetInputField(
            value = set.weight,
            placeholder = set.suggestedWeight.ifBlank { "—" },
            onValueChange = onWeightChange,
            enabled = !set.isDone,
            textStyle = activeStyle,
            modifier = Modifier.weight(1.3f)
        )

        SetInputField(
            value = set.reps,
            placeholder = set.targetReps.toString(),
            onValueChange = onRepsChange,
            enabled = !set.isDone,
            textStyle = activeStyle,
            modifier = Modifier.weight(1.3f)
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(
                onClick = onDoneToggle,
                modifier = Modifier.size(32.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (set.isDone) AppGreen else Gray.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (set.isDone) {
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

@Composable
private fun SetInputField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = if (enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                style = textStyle.copy(color = Gray.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            )
        }
        BasicTextField(
            value = value,
            onValueChange = { if (enabled) onValueChange(it.filter { c -> c.isDigit() || c == '.' }) },
            textStyle = textStyle,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            cursorBrush = SolidColor(AppGreen),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        )
    }
}