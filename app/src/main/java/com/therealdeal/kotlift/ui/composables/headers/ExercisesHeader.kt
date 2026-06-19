package com.therealdeal.kotlift.ui.composables.headers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealdeal.kotlift.ui.composables.buttons.HeaderBackButton
import com.therealdeal.kotlift.ui.composables.commonComponents.SearchBar
import com.therealdeal.kotlift.ui.composables.chips.FilterRow

@Composable
fun ExercisesHeader(
    searchText: String,
    onSearchChange: (String) -> Unit,
    muscleGroups: List<String>,
    selectedMuscle: String,
    onMuscleSelect: (String) -> Unit,
    onBackClick: () -> Unit,
    onHeightMeasured: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates -> onHeightMeasured(coordinates.size.height) },
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.96f),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 14.dp, bottom = 4.dp)
        ) {
            HeaderBackButton(
                onClick = onBackClick,
                modifier = Modifier.padding(start = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Database Esercizi",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            SearchBar(
                value = searchText,
                onValueChange = onSearchChange,
                placeholderText = "Cerca esercizio (es. Piegamenti)...",
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilterRow(
                categories = muscleGroups,
                selectedCategory = selectedMuscle,
                onCategorySelected = onMuscleSelect,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}