package com.huertohogar.huertohogarmovil.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Creamos un DataStore para guardar la sesión
private val Context.dataStore by preferencesDataStore(name = "sesion_usuario")

class SessionManager(private val context: Context) {

    companion object {
        val KEY_USER_ID = intPreferencesKey("user_id")
        const val NO_USER_ID = -1
    }

    // Flujo que emite el ID del usuario actual. Emite NO_USER_ID si no hay nadie logueado.
    val currentUserId: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_USER_ID] ?: NO_USER_ID
        }

    // Función para guardar el ID del usuario al iniciar sesión
    suspend fun login(userId: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = userId
        }
    }

    // Función para borrar el ID al cerrar sesión
    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_USER_ID)
        }
    }
}