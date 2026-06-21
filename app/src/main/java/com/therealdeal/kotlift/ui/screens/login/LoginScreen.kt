package com.therealdeal.kotlift.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.therealdeal.kotlift.ui.composables.headers.AuthHeader
import com.therealdeal.kotlift.ui.composables.login.AppTextField
import com.therealdeal.kotlift.ui.composables.login.AuthButton
import com.therealdeal.kotlift.ui.composables.login.ClickableFooterText
import com.therealdeal.kotlift.navigation.LoginNavigation
import com.therealdeal.kotlift.ui.theme.BackgroundDark
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    onNavigate: (LoginNavigation) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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

            LaunchedEffect(uiState.loggedInUser) {
                uiState.loggedInUser?.let { onNavigate(LoginNavigation.Home) }
            }

            AuthHeader(
                title = "Welcome!",
                subtitle = "Your fitness journey continues here"
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = "Email",
                leadingIcon = Icons.Filled.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            AppTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Password",
                leadingIcon = Icons.Filled.Lock,
                isPassword = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (!uiState.isLoading) viewModel.login()
                    }
                ),
                isError = uiState.errorMessage != null
            )

            Spacer(modifier = Modifier.height(8.dp))

            AuthButton(
                text = "Login",
                onClick = { viewModel.login() },
                enabled = uiState.email.isNotBlank() && uiState.password.isNotBlank()
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