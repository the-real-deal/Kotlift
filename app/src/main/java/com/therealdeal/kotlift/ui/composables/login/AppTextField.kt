package com.therealdeal.kotlift.ui.composables.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.saveable.rememberSaveable
import com.therealdeal.kotlift.ui.theme.AppGreen
import com.therealdeal.kotlift.ui.theme.SurfaceDark
import com.therealdeal.kotlift.ui.theme.SurfaceVariantDark
import com.therealdeal.kotlift.ui.theme.TextPrimary
import com.therealdeal.kotlift.ui.theme.TextSecondary

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, color = TextSecondary) },
        leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null, tint = TextSecondary) },
        trailingIcon = if (isPassword) {
            {
                val image = if (passwordVisible) Icons.Filled.Favorite else Icons.Filled.AccountBox
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null, tint = TextSecondary)
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        isError = isError,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = SurfaceDark,
            unfocusedContainerColor = SurfaceDark,
            cursorColor = AppGreen,
            focusedBorderColor = AppGreen,
            unfocusedBorderColor = SurfaceVariantDark,
            focusedLabelColor = AppGreen,
            unfocusedLabelColor = TextSecondary
        )
    )
}