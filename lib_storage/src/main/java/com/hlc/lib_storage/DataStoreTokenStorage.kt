package com.hlc.lib_storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "app_storage")

class DataStoreTokenStorage(private val context: Context) : TokenStorage {
    private val tokenKey = stringPreferencesKey("token")

    override suspend fun getToken(): String? {
        val preferences: Preferences = context.dataStore.data.first()
        return preferences[tokenKey]
    }

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }
}
