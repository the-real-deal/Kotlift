package com.therealdeal.kotlift.ui.composables.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun WorkoutCard(
    workout: Workout,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = workout.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = workout.description ?: "No description available",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val chipColor = when (workout.difficulty) {
                    WorkoutDifficulty.Beginner -> MaterialTheme.colorScheme.primary
                    WorkoutDifficulty.Intermediate -> IconYellow
                    WorkoutDifficulty.Advanced -> MaterialTheme.colorScheme.error
                }
                GenericChip(
                    text = workout.difficulty.toString(),
                    size = ChipSize.Small,
                    backgroundColor = chipColor.copy(alpha = 0.2f),
                    textColor = chipColor
                )

                val durationText =  workout.estimatedTimeMinutes.toString() + " min"
                GenericChip(
                    text = durationText,
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