package com.therealdeal.kotlift.ui.screens.register

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import com.therealdeal.kotlift.ui.composables.headers.AuthHeader
import com.therealdeal.kotlift.ui.composables.login.AppTextField
import com.therealdeal.kotlift.ui.composables.login.AuthButton
import com.therealdeal.kotlift.ui.composables.login.ClickableFooterText

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.therealdeal.kotlift.navigation.RegisterNavigation
import com.therealdeal.kotlift.ui.composables.login.ErrorSnackBarHost
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = koinViewModel(),
    onNavigate: (RegisterNavigation) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val ctx = LocalContext.current

    LaunchedEffect(uiState.isSuccess) {
        if(uiState.isSuccess) {
            Toast.makeText(ctx, "Registration complete!", Toast.LENGTH_SHORT).show()
            onNavigate(RegisterNavigation.Home)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackBarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { ErrorSnackBarHost(hostState = snackBarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                    value = uiState.username,
                    onValueChange = viewModel::onUsernameChange,
                    label = "Username",
                    leadingIcon = Icons.Filled.Person,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                AppTextField(
                    value = uiState.confirmPassword,
                    onValueChange = viewModel::onPasswordConfirmChange,
                    label = "Confirm password",
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            if (!uiState.isLoading) viewModel.register()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                AuthButton(
                    text = "Register",
                    onClick = viewModel::register
                )

                Spacer(modifier = Modifier.height(16.dp))

                ClickableFooterText(
                    normalText = "Already have an account?",
                    clickableText = "Login",
                    onClick = { onNavigate(RegisterNavigation.Login) }
                )
            }
        }
    }
}