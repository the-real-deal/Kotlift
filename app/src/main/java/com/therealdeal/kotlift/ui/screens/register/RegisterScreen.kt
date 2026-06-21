package com.therealdeal.kotlift.ui.screens.register

import com.therealdeal.kotlift.ui.composables.headers.AuthHeader
import com.therealdeal.kotlift.ui.composables.login.AppTextField
import com.therealdeal.kotlift.ui.composables.login.AuthButton
import com.therealdeal.kotlift.ui.composables.login.ClickableFooterText

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.therealdeal.kotlift.navigation.RegisterNavigation

@Composable
fun RegisterScreen(
    onNavigate: (RegisterNavigation) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

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
                title = "Start Lifting Smarter",
                subtitle = "Create your free Kotlift account"
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = "Username",
                leadingIcon = Icons.Filled.Person,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

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

            AppTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm password",
                leadingIcon = Icons.Filled.Lock,
                isPassword = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val isFormValid = name.isNotBlank() &&
                    email.isNotBlank() &&
                    password.isNotBlank() &&
                    password == confirmPassword

            AuthButton(
                text = "Register",
                onClick = { onNavigate(RegisterNavigation.Home) },
                enabled = isFormValid
            )

            Spacer(modifier = Modifier.height(16.dp))

            ClickableFooterText(
                normalText = "Hai già un account?",
                clickableText = "Login",
                onClick = {onNavigate(RegisterNavigation.Login)}
            )
        }
    }
}