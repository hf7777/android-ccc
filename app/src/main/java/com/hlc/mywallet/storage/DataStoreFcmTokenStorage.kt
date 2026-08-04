package com.hlc.mywallet.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * 基于 DataStore 的 FCM Token 存储实现
 */
class DataStoreFcmTokenStorage(private val context: Context) : FcmTokenStorage {
    private val tokenKey = stringPreferencesKey("fcm_token")

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
