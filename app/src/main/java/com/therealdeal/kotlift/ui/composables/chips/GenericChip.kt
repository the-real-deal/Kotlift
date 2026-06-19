package com.therealdeal.kotlift.ui.composables.chips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ChipSize(
    val iconSize: Dp,
    val fontSize: TextUnit,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val spaceBetween: Dp
) {
    Small(iconSize = 12.dp, fontSize = 11.sp, horizontalPadding = 8.dp, verticalPadding = 4.dp, spaceBetween = 4.dp),
    Medium(iconSize = 16.dp, fontSize = 13.sp, horizontalPadding = 12.dp, verticalPadding = 6.dp, spaceBetween = 6.dp),
    Large(iconSize = 20.dp, fontSize = 15.sp, horizontalPadding = 16.dp, verticalPadding = 8.dp, spaceBetween = 8.dp)
}

@Composable
fun GenericChip(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    size: ChipSize = ChipSize.Medium,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .padding(horizontal = size.horizontalPadding, vertical = size.verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(size.spaceBetween)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(size.iconSize),
                tint = iconColor
            )
        }

        Text(
            text = text,
            fontSize = size.fontSize,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}