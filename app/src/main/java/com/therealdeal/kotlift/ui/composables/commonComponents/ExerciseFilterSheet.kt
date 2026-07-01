package com.therealdeal.kotlift.ui.composables.commonComponents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealdeal.kotlift.model.ExerciseFilters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseFilterSheet(
    muscles: List<String>,
    bodyParts: List<String>,
    equipments: List<String>,
    currentFilters: ExerciseFilters,
    onApply: (ExerciseFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMuscle by remember { mutableStateOf(currentFilters.targetMuscle) }
    var selectedBodyPart by remember { mutableStateOf(currentFilters.bodyPart) }
    var selectedEquipment by remember { mutableStateOf(currentFilters.equipment) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = {
                    selectedMuscle = null
                    selectedBodyPart = null
                    selectedEquipment = null
                }) {
                    Text("Clear all", color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            FilterSection(
                title = "Target Muscle",
                items = muscles,
                selected = selectedMuscle,
                onSelect = { selectedMuscle = if (selectedMuscle == it) null else it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            FilterSection(
                title = "Body Part",
                items = bodyParts,
                selected = selectedBodyPart,
                onSelect = { selectedBodyPart = if (selectedBodyPart == it) null else it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            FilterSection(
                title = "Equipment",
                items = equipments,
                selected = selectedEquipment,
                onSelect = { selectedEquipment = if (selectedEquipment == it) null else it }
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    onApply(ExerciseFilters(
                        targetMuscle = selectedMuscle,
                        bodyPart = selectedBodyPart,
                        equipment = selectedEquipment
                    ))
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Apply filters", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    items: List<String>,
    selected: String?,
    onSelect: (String) -> Unit
) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
    Spacer(modifier = Modifier.height(10.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items) { item ->
            val isSelected = selected == item
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(item) },
                label = {
                    Text(
                        text = item.replaceFirstChar { it.uppercase() },
                        fontSize = 13.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}