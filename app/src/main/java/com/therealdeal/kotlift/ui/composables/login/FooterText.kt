package com.therealdeal.kotlift.ui.composables.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.therealdeal.kotlift.ui.theme.AppGreenLight
import com.therealdeal.kotlift.ui.theme.TextSecondary

@Composable
fun ClickableFooterText(
    normalText: String,
    clickableText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(text = normalText, color = TextSecondary)
        Text(
            text = " $clickableText",
            color = AppGreenLight,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onClick() }
        )
    }
}