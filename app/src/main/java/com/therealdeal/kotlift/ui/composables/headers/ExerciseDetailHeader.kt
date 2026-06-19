package com.therealdeal.kotlift.ui.composables.headers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import coil3.ImageLoader
//import coil3.compose.AsyncImage
//import coil3.decode.GifDecoder
//import coil3.request.ImageRequest
import com.therealdeal.kotlift.ui.composables.buttons.HeaderBackButton
import com.therealdeal.kotlift.ui.theme.Transparent

val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e3/Hitler_speech.gif"


@Composable
fun ExerciseDetailHeader(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

//    val gifImageLoader = ImageLoader.Builder(context)
//        .components {
//            add(GifDecoder.Factory())
//        }
//        .build()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
//        AsyncImage(
//            model = ImageRequest.Builder(context)
//                .data(if (imageUrl.isNotEmpty()) imageUrl else "https://i.giphy.com/media/v1.Y2lkPTc5MGI3NjExbW9wN3A0YmthNXkyY3V0Y3Rndm5wNWhuYW96ZWh6N3B0Zms0Yms4dyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/ICOgUNjpvO0PC/giphy.gif")
//                .crossfade(true)
//                .placeholder(R.drawable.dark_header)
//                .error(R.drawable.stat_notify_error)
//                .build(),
//            contentDescription = "Immagine esercizio",
//            imageLoader = gifImageLoader,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.fillMaxSize()
//        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                            Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                        ),
                        startY = 0f
                    )
                )
        )

        HeaderBackButton(
            onClick = onBackClick,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}