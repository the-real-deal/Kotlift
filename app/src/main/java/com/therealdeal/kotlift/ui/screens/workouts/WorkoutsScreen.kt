package com.therealdeal.kotlift.ui.screens.workouts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealdeal.kotlift.ui.composables.cards.WorkoutCard
import com.therealdeal.kotlift.ui.composables.headers.WorkoutsTopBar
import com.therealdeal.kotlift.ui.composables.chips.FilterRow
import com.therealdeal.kotlift.ui.theme.Gray
import com.therealdeal.kotlift.navigation.WorkoutsNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsScreen(
    onNavigate: (WorkoutsNavigation) -> Unit,
    innerPadding: PaddingValues
) {
    val categories = remember { listOf("Tutti", "beginner", "intermediate", "advanced") }

    Scaffold(
        modifier = Modifier.padding(
            top = innerPadding.calculateTopPadding()
        ),
        topBar = {

            WorkoutsTopBar(
                searchText = "",
                onSearchTextChange = {},
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            val currentCategory = "Tutti"

            FilterRow(
                categories = categories,
                selectedCategory = currentCategory,
                onCategorySelected = {  },
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val state = WorkoutsUiState.Success("chiappe")
                when (state) {
//                    is WorkoutsUiState.Loading -> {
//                        CircularProgressIndicator()
//                    }
//
//                    is WorkoutsUiState.Error -> {
//                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                            Text(text = state.message, color = MaterialTheme.colorScheme.error)
//                            Spacer(modifier = Modifier.height(8.dp))
//                            Button(onClick = { }) {
//                                Text("Riprova")
//                            }
//                        }
//                    }

                    is WorkoutsUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Nessun risultato trovato" ,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Gray
                                )
                            }

                            items(10) { workout ->
                                WorkoutCard(
                                    onClick = { onNavigate(WorkoutsNavigation.WorkoutDetail) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}