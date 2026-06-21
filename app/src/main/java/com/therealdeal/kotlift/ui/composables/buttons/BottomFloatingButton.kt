package com.therealdeal.kotlift.ui.composables.buttons

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomFloatingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null
) {
    if (icon != null) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(6.dp),
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            icon = icon,
            text = {
                Text(
                    text = text,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        )
    }
}