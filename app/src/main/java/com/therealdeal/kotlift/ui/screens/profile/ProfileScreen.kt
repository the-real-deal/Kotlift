package com.therealdeal.kotlift.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.therealdeal.kotlift.ui.composables.cards.GoalsCard
import com.therealdeal.kotlift.ui.composables.commonComponents.AchievementsSection
import com.therealdeal.kotlift.ui.composables.headers.ProfileHeaderSection
import com.therealdeal.kotlift.ui.composables.settings.SettingsSection
import com.therealdeal.kotlift.navigation.ProfileNavigation

@Composable
fun ProfileScreen(onNavigate: (ProfileNavigation) -> Unit, innerPadding : PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeaderSection()

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            GoalsCard()

            Spacer(modifier = Modifier.height(20.dp))

            AchievementsSection()
3
            Spacer(modifier = Modifier.height(20.dp))

            SettingsSection()

            Spacer(modifier = Modifier.height(20.dp))

            ProfileActions()

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}


@Composable
fun ProfileActions() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Update Profile", color = MaterialTheme.colorScheme.onSurface)
        }

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out & Clear Data", color = MaterialTheme.colorScheme.error)
        }
    }
}