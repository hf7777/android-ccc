package com.hlc.mywallet.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreTokenStorage(private val context: Context) : TokenStorage {
    private val tokenKey = stringPreferencesKey("token")

    override suspend fun getToken(): String? {
        val preferences: Preferences = context.appDataStore.data.first()
        return preferences[tokenKey]
    }

    override fun getTokenFlow(): Flow<String?> {
        return context.appDataStore.data.map { preferences ->
            preferences[tokenKey]
        }
    }

    override suspend fun saveToken(token: String) {
        context.appDataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    override suspend fun clearToken() {
        context.appDataStore.edit { preferences ->
            preferences.remove(tokenKey)
        }
    }
}
