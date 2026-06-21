package com.therealdeal.kotlift.ui.composables.commonComponents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modello dati per gli achievement
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val unlockedDate: String? = null,
    val progress: AchievementProgress? = null
)

data class AchievementProgress(
    val current: Int,
    val max: Int
)

@Composable
fun AchievementsSection() {
    val achievements = listOf(
        Achievement("first_steps", "First Steps", "Complete your very first workout. Every journey starts with a single rep.", "Beginner", 50, isUnlocked = true, unlockedDate = "12 Jun 2025"),
        Achievement("on_fire", "On Fire", "Log workouts 7 days in a row. Keep the streak alive!", "Consistency", 150, progress = AchievementProgress(3, 7)),
        Achievement("dedicated", "Dedicated", "Complete 30 total workouts. Dedication bridges goals and accomplishment.", "Consistency", 300, progress = AchievementProgress(11, 30)),
        Achievement("record_breaker", "Record Breaker", "Set a new personal record on any exercise.", "Performance", 200),
        Achievement("self_aware", "Self Aware", "Log your body measurements for the first time.", "Analytics", 100),
        Achievement("unstoppable", "Unstoppable", "Reach a 30-day workout streak. True legends never stop.", "Mastery", 500, progress = AchievementProgress(3, 30))
    )

    var selectedAchievement by remember { mutableStateOf<Achievement?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Achievements",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val unlockedCount = achievements.count { it.isUnlocked }
                    Text(
                        text = "$unlockedCount / ${achievements.size}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                achievements.chunked(3).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowItems.forEach { achievement ->
                            BadgeItem(
                                achievement = achievement,
                                onClick = { selectedAchievement = achievement }
                            )
                        }
                    }
                }
            }
        }
    }

    selectedAchievement?.let { achievement ->
        AchievementDetailSheet(
            achievement = achievement,
            onDismiss = { selectedAchievement = null }
        )
    }
}

@Composable
fun BadgeItem(achievement: Achievement, onClick: () -> Unit) {
    val containerColor = if (achievement.isUnlocked)
        Color(0xFF1D9E75).copy(alpha = 0.15f)
    else
        MaterialTheme.colorScheme.surfaceContainer

    val iconTint = if (achievement.isUnlocked)
        Color(0xFF0F6E56)
    else
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(85.dp)
            .background(color = containerColor, shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.EmojiEvents,
                contentDescription = achievement.name,
                tint = iconTint,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = achievement.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementDetailSheet(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header: icona + titolo + stato
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (achievement.isUnlocked)
                        Color(0xFF1D9E75).copy(alpha = 0.15f)
                    else
                        MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Outlined.EmojiEvents,
                            contentDescription = null,
                            tint = if (achievement.isUnlocked) Color(0xFF0F6E56)
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = achievement.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (achievement.isUnlocked) "✓ Unlocked" else "Locked",
                        fontSize = 13.sp,
                        color = if (achievement.isUnlocked) Color(0xFF0F6E56)
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = achievement.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

            // Dettagli
            DetailRow(label = "Category", value = achievement.category)
            DetailRow(label = "XP reward", value = "+${achievement.xpReward} XP")

            if (achievement.isUnlocked && achievement.unlockedDate != null) {
                DetailRow(label = "Unlocked on", value = achievement.unlockedDate)
            }

            // Barra di progresso (solo se non sbloccato e con progresso)
            if (!achievement.isUnlocked && achievement.progress != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Progress", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text(
                        text = "${achievement.progress.current} / ${achievement.progress.max}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val animatedProgress by animateFloatAsState(
                    targetValue = achievement.progress.current.toFloat() / achievement.progress.max,
                    animationSpec = tween(500),
                    label = "progress"
                )

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF1D9E75),
                    trackColor = MaterialTheme.colorScheme.surfaceContainer
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}