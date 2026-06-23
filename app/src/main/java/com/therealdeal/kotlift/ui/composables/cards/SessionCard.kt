package com.therealdeal.kotlift.ui.composables.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.therealdeal.kotlift.model.Session
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SessionCard(session: Session) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formatted = session.startedAt
                ?.toLocalDateTime(TimeZone.currentSystemDefault())
                ?.date
                ?.toJavaLocalDate()
                ?.format(formatter)?: "No date"

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.workoutTitle,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatted,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                session.actualDurationMinutes?.let {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$it min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            session.totalWeightLifted?.let {
                Spacer(modifier = Modifier.width(16.dp)) // Ensures spacing between text and weight
                Text(
                    text = "${it.toInt()} kg",
                    style = MaterialTheme.typography.titleLarge, // Bumped up slightly for better visual hierarchy
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}