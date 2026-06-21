package com.therealdeal.kotlift.data.repository

import android.util.Log
import com.therealdeal.kotlift.data.remote.ProfileDTO
import com.therealdeal.kotlift.model.Profile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
//import io.github.jan.supabase.storage.storage [todo] maybe save images
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put


/**
 * Authentication repository, handles login and new account creation.
 * Uses supabase authentication module, postgres and storage
 */
class AuthRepository(
    private val client: SupabaseClient
) {

    // Developer credentials:
    // username: developer, email dev@developer.com, password: develop123
    suspend fun login(email: String, password: String): Result<Profile> = runCatching {
        Log.d("AUTH", "Logging in as: $email")

        try {
            client.auth.signInWith(Email) {
                this.email    = email
                this.password = password
            }
        } catch (e: Exception) {
            Log.e("AUTH", "Supabase Error: ${e.message}")
            error("Invalid Credential!")
        }

        getCurrentUser() ?: error("Unable to fetch user detail")
    }

    suspend fun getCurrentUser(): Profile? {
        client.auth.awaitInitialization()
        val user = client.auth.currentUserOrNull()
        Log.i("AUTH", "Current user: '$user'")

        val uid = user?.id ?: return null

        return try {
            val response = client.postgrest["profiles"]
                .select(Columns.raw("id, updated_at, profile_picture, day_streak, total_sessions, unlocked_achievements_count, username")) {
                    filter { eq("id", uid) }
                }
            Log.i("AUTH", "Response: ${response.data}")

            val profileDto = response.decodeSingleOrNull<ProfileDTO>()
                ?: error("Profile not found in database!")

            profileDto.toDomain(user.email)

        } catch (e: Exception) {
            Log.e("AUTH", "Error fetching user profile: ${e.message}")
            null
        }
    }

    suspend fun registerUser(
        username: String,
        email: String,
        password: String
    ): Result<Unit> = runCatching {

        val signUpMetadata = buildJsonObject {
            put("username", username)
        }

        // Sign up via Supabase Auth
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            this.data = signUpMetadata
        }

        Log.i("AUTH", "User registered successfully with email: $email")
    }

    suspend fun logout(): Result<Unit> = runCatching {
        client.auth.signOut()
    }
}