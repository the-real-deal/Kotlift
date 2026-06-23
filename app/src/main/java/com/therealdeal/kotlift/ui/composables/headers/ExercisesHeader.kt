package com.therealdeal.kotlift.ui.composables.headers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealdeal.kotlift.ui.composables.buttons.HeaderBackButton
import com.therealdeal.kotlift.ui.composables.commonComponents.SearchBar

@Composable
fun ExercisesHeader(
    searchText: String,
    onSearchChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onHeightMeasured: (Int) -> Unit,
    filterBadgeCount: Int,
    onFilterClick: () -> Unit,
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
                .padding(top = 14.dp, bottom = 12.dp)
        ) {
            HeaderBackButton(
                onClick = onBackClick,
                modifier = Modifier.padding(start = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Exercise Library",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchBar(
                    value = searchText,
                    onValueChange = onSearchChange,
                    placeholderText = "Search exercise...",
                    modifier = Modifier.weight(1f)
                )

                BadgedBox(
                    badge = {
                        if (filterBadgeCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = filterBadgeCount.toString(),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                ) {
                    FilledTonalIconButton(
                        onClick = onFilterClick,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filters"
                        )
                    }
                }
            }
        }
    }
}