package com.therealdeal.kotlift.ui.composables.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.therealdeal.kotlift.ui.composables.chips.ChipSize
import com.therealdeal.kotlift.ui.composables.chips.GenericChip

@Composable
fun ExerciseGridCard(
    name: String,
    category: String,
    target: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current

//    val gifImageLoader = ImageLoader.Builder(context)
//        .components {
//            add(GifDecoder.Factory())
//        }
//        .build()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        onClick = onClick
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
//                AsyncImage(
//                    model = ImageRequest.Builder(context)
//                        .data(if (!imageUrl.isNullOrEmpty()) imageUrl else "https://i.giphy.com/media/v1.Y2lkPTc5MGI3NjExbW9wN3A0YmthNXkyY3V0Y3Rndm5wNWhuYW96ZWh6N3B0Zms0Yms4dyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/ICOgUNjpvO0PC/giphy.gif")
//                        .crossfade(true)
//                        .placeholder(R.drawable.dark_header)
//                        .error(R.drawable.stat_notify_error)
//                        .build(),
//                    contentDescription = "Immagine esercizio $name",
//                    imageLoader = gifImageLoader,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.fillMaxSize()
//                )

                Box(modifier = Modifier.padding(8.dp)) {
                    GenericChip(
                        text = category,
                        size = ChipSize.Small,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                        textColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = target,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
