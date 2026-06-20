package com.therealdeal.kotlift.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.therealdeal.kotlift.ui.composables.headers.AuthHeader
import com.therealdeal.kotlift.ui.composables.login.AppTextField
import com.therealdeal.kotlift.ui.composables.login.AuthButton
import com.therealdeal.kotlift.ui.composables.login.ClickableFooterText
import com.therealdeal.kotlift.navigation.LoginNavigation

@Composable
fun LoginScreen(
    onNavigate: (LoginNavigation) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AuthHeader(
                title = "Welcome!",
                subtitle = "Your fitness journey continues here"
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Filled.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            AppTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Filled.Lock,
                isPassword = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(8.dp))

            AuthButton(
                text = "Login",
                onClick = { onNavigate(LoginNavigation.Home) },
                enabled = email.isNotBlank() && password.isNotBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ClickableFooterText(
                normalText = "Don't have an account?",
                clickableText = "Register",
                onClick = { onNavigate(LoginNavigation.Register) }
            )
        }
    }
}