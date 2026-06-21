package com.therealdeal.kotlift.ui.baseAuthentication


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therealdeal.kotlift.model.Profile
import com.therealdeal.kotlift.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Base view model that handles common function for authentication.
 * All view models that requires an authenticated user will inherit from this.
 */
abstract class BaseViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    /**
     * Event flow to signal the ui that a login is necessary.
     * Emitted when the session is terminated.
     */
    private val _requireLoginEvent = MutableSharedFlow<Unit>()
    val requireLoginEvent = _requireLoginEvent.asSharedFlow()

    /**
     * Logs out the user and notifies the UI by the means of [requireLoginEvent].
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _requireLoginEvent.emit(Unit)
        }
    }

    /**
     * Executes an action that requires the authenticated user.
     * If the user is not authenticated a logout is executed.
     *
     * @param action Lambda that receives the [Profile] object and executed in the view model scope.
     */
    protected fun withAuth(action: suspend (Profile) -> Unit) {
        viewModelScope.launch {
            val user = try {
                authRepository.getCurrentUser()
            } catch (e: Exception) {
                Log.e("AUTH_CHECK", "Authentication error: ${e.message}")
                null
            }

            // If the user is valid execute the action, logout otherwise
            user?.let { action(it) } ?: logout()
        }
    }

    /**
     * Verifies that the user is authenticated to perform an action.
     * Does not pass the Profile object to the lambda function.
     *
     * @param action Lambda to call to perform the action
     */
    protected fun requireAuth(action: suspend () -> Unit) {
        viewModelScope.launch {
            val user = try {
                authRepository.getCurrentUser()
            } catch (e: Exception) {
                Log.e("AUTH_CHECK", "Original error: ${e.message}")
                null
            }

            user?.let { action() } ?: logout()
        }
    }
}