package com.therealdeal.kotlift.ui.baseAuthentication

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.koinViewModel

/**
 * Global communication channel for authentication actions.
 * Uses compositionLocal pattern to avoid having a callback on each screen function.
 */
val LocalAuthActions = staticCompositionLocalOf<() -> Unit> {
    error("No AuthActions provided")
}

/**
 * Universal wrapper for authentication required screens
 * * @param T The specific view model.
 * Has to inherit from BaseViewModel.
 * @param content The content of the UI that will receive the view model.
 */
@Composable
inline fun <reified T : BaseViewModel> AuthenticatedScreen(
    crossinline content: @Composable (T) -> Unit
) {
    val onNavigateToLogin = LocalAuthActions.current

    val viewModel: T = koinViewModel()

    val context = LocalContext.current

    // Listens to eventual logout signals.
    LaunchedEffect(Unit) {
        viewModel.requireLoginEvent.collect {
            Toast.makeText(context, "Session timed out", Toast.LENGTH_LONG).show()
            onNavigateToLogin()
        }
    }

    content(viewModel)
}