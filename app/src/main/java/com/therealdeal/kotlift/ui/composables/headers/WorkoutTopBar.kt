package com.therealdeal.kotlift.ui.composables.headers

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealdeal.kotlift.ui.composables.commonComponents.SearchBar

@Composable
fun WorkoutsTopBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(top = 16.dp)) {
        Text(
            text = "Your Workout",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))

        SearchBar(
            value = searchText,
            onValueChange = onSearchTextChange
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}