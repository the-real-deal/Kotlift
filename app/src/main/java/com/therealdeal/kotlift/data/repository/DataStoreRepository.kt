package com.therealdeal.kotlift.data.repository

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.therealdeal.kotlift.model.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.core.net.toUri

class DataStoreRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
        private fun profileImageKey(userId: String) =
            stringPreferencesKey("profile_image_uri_$userId")
    }

    val theme = dataStore.data.map { preferences ->
        try {
            Theme.valueOf(preferences[THEME_KEY] ?: "System")
        } catch (_: Exception) {
            Theme.System
        }
    }

    suspend fun setTheme(theme: Theme) = dataStore.edit { preferences ->
        preferences[THEME_KEY] = theme.toString()
    }

    fun profilePictureURI(userId: String): Flow<Uri?> =
        dataStore.data.map { preferences ->
            try {
                preferences[profileImageKey(userId)]?.toUri()
            } catch (_: Exception) {
                null
            }
        }

    suspend fun setProfilePictureURI(userId: String, uri: Uri) {
        dataStore.edit { preferences ->
            preferences[profileImageKey(userId)] = uri.toString()
        }
    }

    suspend fun removeProfilePictureURI(userId: String) {
        dataStore.edit { preferences ->
            preferences.remove(profileImageKey(userId))
        }
    }
}
