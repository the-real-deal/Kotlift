package com.therealdeal.kotlift.ui.baseAuthentication
//
//
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.almaaule.domain.model.User
//import com.example.almaaule.domain.repository.AuthRepository
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.asSharedFlow
//import kotlinx.coroutines.launch
//
///**
// * Base view model that handles common function for authentication.
// * All view models that requires an authenticated user will inherit from this.
// */
//abstract class BaseViewModel(
//    private val authRepository: AuthRepository
//) : ViewModel() {
//
//    /**
//     * Flusso di eventi per segnalare alla UI che è necessario tornare alla schermata di Login.
//     * Viene emesso quando la sessione scade o l'account viene disattivato.
//     */
//    private val _requireLoginEvent = MutableSharedFlow<Unit>()
//    val requireLoginEvent = _requireLoginEvent.asSharedFlow()
//
//    /**
//     * Esegue il logout dell'utente tramite il repository e notifica la UI tramite [requireLoginEvent].
//     */
//    fun logout() {
//        viewModelScope.launch {
//            authRepository.logout()
//            _requireLoginEvent.emit(Unit)
//        }
//    }
//
//    /**
//     * Esegue un'azione che richiede i dati dell'utente attualmente loggato.
//     * Se l'utente non è autenticato o i suoi dati non sono recuperabili (es. account disattivato),
//     * viene eseguito automaticamente il logout.
//     *
//     * @param action Lambda che riceve l'oggetto [User] e viene eseguita nel viewModelScope.
//     */
//    protected fun withAuth(action: suspend (User) -> Unit) {
//        viewModelScope.launch {
//            val user = try {
//                authRepository.getCurrentUser()
//            } catch (e: Exception) {
//                Log.e("AUTH_CHECK", "Original error: ${e.message}")
//                null
//            }
//
//            // Se l'utente è valido esegue l'azione, altrimenti forza il reindirizzamento al login
//            user?.let { action(it) } ?: logout()
//        }
//    }
//
//    /**
//     * Verifica che l'utente sia autenticato prima di eseguire un'azione.
//     * A differenza di [withAuth], non fornisce l'oggetto User alla lambda.
//     * Utile per azioni che devono solo essere protette ma non dipendono dai dati specifici dell'utente.
//     *
//     * @param action Lambda da eseguire se l'utente è autenticato.
//     */
//    protected fun requireAuth(action: suspend () -> Unit) {
//        viewModelScope.launch {
//            val user = try {
//                authRepository.getCurrentUser()
//            } catch (e: Exception) {
//                Log.e("AUTH_CHECK", "Original error: ${e.message}")
//                null
//            }
//
//            // Se l'utente è valido esegue l'azione, altrimenti forza il reindirizzamento al login
//            user?.let { action() } ?: logout()
//        }
//    }
//}