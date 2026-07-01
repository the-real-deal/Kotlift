package com.therealdeal.kotlift.ui.composables.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealdeal.kotlift.model.Workout
import com.therealdeal.kotlift.model.WorkoutDifficulty
import com.therealdeal.kotlift.ui.composables.chips.ChipSize
import com.therealdeal.kotlift.ui.composables.chips.GenericChip
import com.therealdeal.kotlift.ui.theme.IconYellow
import com.therealdeal.kotlift.ui.theme.IconYellowText

@Composable
fun WorkoutCard(
    workout: Workout,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    data class ChipColorData(val chipText: Color, val chipBackground: Color)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = workout.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete workout",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Text(
                text = workout.description ?: "No description available",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val chipColor = when (workout.difficulty) {
                    WorkoutDifficulty.Beginner -> ChipColorData(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                    WorkoutDifficulty.Intermediate -> ChipColorData(
                        IconYellow,
                        IconYellow.copy(alpha = 0.2f)
                    )
                    WorkoutDifficulty.Advanced -> ChipColorData(
                        MaterialTheme.colorScheme.error,
                        MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                    )
                    null -> ChipColorData(
                        MaterialTheme.colorScheme.onSurfaceVariant,
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                }

                if (workout.difficulty != null) {
                    GenericChip(
                        text = workout.difficulty.toString(),
                        size = ChipSize.Small,
                        backgroundColor = chipColor.chipBackground,
                        textColor = chipColor.chipText
                    )
                }

                if (workout.estimatedTimeMinutes != null) {
                    GenericChip(
                        text = "${workout.estimatedTimeMinutes} min",
                        icon = Icons.Filled.History,
                        size = ChipSize.Small,
                        backgroundColor = Color.Transparent,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}