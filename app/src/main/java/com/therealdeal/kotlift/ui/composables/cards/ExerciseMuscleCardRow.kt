package com.therealdeal.kotlift.ui.composables.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hub
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun ExerciseMuscleCardRow(
    primaryTarget: String,
    secondaryTarget: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ExerciseInfoCard(
            icon = Icons.Filled.FitnessCenter,
            title = "Muscolo Target",
            value = primaryTarget,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        ExerciseInfoCard(
            icon = Icons.Filled.Hub,
            title = "Sinergici",
            value = secondaryTarget,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}