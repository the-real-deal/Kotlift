package com.therealdeal.kotlift.ui.composables.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.therealdeal.kotlift.ui.composables.chips.ChipSize
import com.therealdeal.kotlift.ui.composables.chips.GenericChip
@Composable
fun ExerciseDetailTags(
    difficulty: String,
    category: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GenericChip(
            text = difficulty,
            size = ChipSize.Medium,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            textColor = MaterialTheme.colorScheme.onPrimaryContainer
        )

        GenericChip(
            text = category,
            size = ChipSize.Medium,
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            textColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}